package org.kari.call;

import gnu.trove.set.hash.THashSet;

import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.kari.call.io.CallClientSocketFactory;
import org.kari.call.io.DefaultCallClientSocketFactory;
import org.kari.call.io.IOFactory;

/**
 * Handles making connections to server
 *
 * @author kari
 */
public final class CallClient extends CallBase {
    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".client");
    private static final int CLEANUP_PERIOD = 20;

    static {
        CallType.initCache();
    }

    private final ClientKey mClienKey;
    private final CallClientSocketFactory mSocketFactory;

    /**
     * All instantiated handlers. Allow closing of connections even if they
     * were not returned back to pool properly
     */
    private final Set<ClientHandler> mHandlers = new THashSet<ClientHandler>();

    /**
     * Currently available handlers:
     *
     * <p>Map of (sessionId, List of (handler))
     */
    private final IdentityHashMap<Object, List<ClientHandler>> mAvailable =
            new IdentityHashMap<Object, List<ClientHandler>>();

    /**
     * Pending handlers to kill
     */
    private final Set<ClientHandler> mPending = new THashSet<ClientHandler>();

    private int mCleanupCounter;
    private final List<ClientHandler> mRemovedHandlers = new ArrayList<ClientHandler>();
    private final List<Object> mRemovedSessions = new ArrayList<Object>();


    /**
     * @param pServerAddress Either actual IP/host or application specific
     * identity for server addresses
     *
     * @param pSocketFactory if null default (plain socket) is used
     * @param pIOFactory if null default is used
     * @param pRegistry If null new registry is created with default resolver
     * @param pCounterEnabled Is counter stats collected
     * @param pReuseObjectStream If true framework reuses Object IO streams
     * created via IOFactory
     */
    public CallClient(
            final String pServerAddress,
            final int pPort,
            final ServiceRegistry pRegistry,
            final IOFactory pIOFactory,
            final CallClientSocketFactory pSocketFactory)
    {
        super(pServerAddress, pPort, pRegistry, pIOFactory);

        mSocketFactory = pSocketFactory != null
                ? pSocketFactory
                        : DefaultCallClientSocketFactory.INSTANCE;

        mClienKey = new ClientKey();
    }

    /**
     * Key to identify handlers for asynchronous calls
     */
    public ClientKey getClientKey() {
        return mClienKey;
    }

    /**
     * Retrieve handler for pSessionId and cleanup possible dead handlers
     * (socket died, etc.)
     *
     * @return null if not found
     */
    private ClientHandler getBySession(Object pSessionId) {
        ClientHandler handler = null;

        List<ClientHandler> handlers = mAvailable.get(pSessionId);
        if (handlers != null && !handlers.isEmpty()) {
            mRemovedHandlers.clear();

            while (handler == null && !handlers.isEmpty()) {
                handler = handlers.remove(handlers.size() - 1);

                if (!handler.isRunning()) {
                    mRemovedHandlers.add(handler);
                    handler = null;
                }
            }

            if (!mRemovedHandlers.isEmpty()) {
                for (ClientHandler removed : mRemovedHandlers) {
                    release(removed);
                }
                mRemovedHandlers.clear();
            }
        }

        return handler;
    }

    /**
     * Store pHandler reference using sessionId
     */
    private void putBySession(ClientHandler pHandler) {
        final Object sessionId = pHandler.getLastSessionId();
        List<ClientHandler> handlers = mAvailable.get(sessionId);
        if (handlers == null) {
            handlers = new ArrayList<ClientHandler>();
            mAvailable.put(sessionId, handlers);
        }
        handlers.add(pHandler);
    }

    private void cleanupBySession() {
        mCleanupCounter++;
        if (mCleanupCounter >= CLEANUP_PERIOD) {
            mRemovedSessions.clear();

            for (Object sessionId : mAvailable.keySet()) {
                List<ClientHandler> handlers = mAvailable.get(sessionId);
                if (handlers.isEmpty()) {
                    mRemovedSessions.add(sessionId);
                }
            }

            if (!mRemovedSessions.isEmpty()) {
                for (Object sessionId : mRemovedSessions) {
                    mAvailable.remove(sessionId);
                }
                mRemovedSessions.clear();
            }

            mCleanupCounter = 0;
        }
    }

    /**
     * Create and register new handler
     */
    private ClientHandler newHandler()
            throws RemoteException
            {
        ClientHandler handler = null;
        try {
            Socket socket = mSocketFactory.createSocket(
                    getServerAddress(),
                    getPort());

            handler = new ClientHandler(socket, this);
            handler.handshake();

            mHandlers.add(handler);
        } catch (Throwable e) {
            if (handler != null) {
                handler.kill();
                handler.free();
            }
            throw new RemoteException("Failed to connect server", e);
        }
        return handler;
    }

    /**
     * Get available handler from pool of handlers or create new handler.
     * Reserved handler *MUST* be returned back to pool after use to avoid
     * memory leaks.
     *
     * <p>USAGE: use try-finally
     * <pre>
     * ClientHandler handler = client.reserve();
     * try {
     *     ...
     * } finally {
     *     client.release(handler);
     * }
     * </pre>
     *
     * @param pSessionId Associated session. Existing handler usinsg pSessionId
     * is preferably get instead of another, to improve "not changed" sessionId
     * logic in call serialization.
     *
     * @throws RemoteException if cannot connect to server
     */
    public synchronized ClientHandler reserve(Object pSessionId) throws RemoteException {
        ClientHandler handler = getBySession(pSessionId);

        if (handler == null) {
            // If no specific session match, pick any session
            for (Object sessionId : mAvailable.keySet()) {
                handler = getBySession(sessionId);
                if (handler != null) {
                    break;
                }
            }

            if (handler == null) {
                handler = newHandler();
            }
        }

        return handler;
    }

    /**
     * Get available handler from pool of handlers or create new handler
     */
    public synchronized void release(ClientHandler pHandler) {
        if (mPending.contains(pHandler)) {
            pHandler.kill();
            pHandler.free();

            mPending.remove(pHandler);
        }

        if (pHandler.isRunning()) {
            putBySession(pHandler);
        } else {
            // ensure socket is properly closed
            pHandler.kill();
            pHandler.free();

            mHandlers.remove(pHandler);
        }

        cleanupBySession();
    }

    /**
     * Close all handlers to enforce restart of connection. Relevant, for
     * example, if server-address is abstraction of actual host IP, and
     * restart of connection is needed after "server URL" change.
     *
     * @param pForceKill if true currently in-use connections are forcefully
     * killed, otherwise those connections are closed softly (i.e. after
     * they are released back to pool). Using force should be avoided, since
     * it kills connections into middle, causing client and server state to
     * be out-of-sync (i.e. call is executed in server side but client side
     * gets error).
     */
    public synchronized void closeHandlers(boolean pForceKill) {
        for (List<ClientHandler> handlers : mAvailable.values()) {
            for (ClientHandler handler : handlers) {
                handler.kill();
                handler.free();

                mHandlers.remove(handler);
            }
        }

        mAvailable.clear();

        if (pForceKill) {
            for (ClientHandler handler : mHandlers) {
                handler.kill();
                // NOTE KI no free; would cause threading violation
            }

            for (ClientHandler handler : mPending) {
                handler.kill();
                // NOTE KI no free; would cause threading violation
            }

            mHandlers.clear();
            mPending.clear();
        } else {
            // Don't kill currently used handlers; avoid distrupting pending
            // calls; kill handlers when they are released
            mPending.addAll(mHandlers);
            mHandlers.clear();
        }
    }
}
