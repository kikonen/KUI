package org.kari.call;

import gnu.trove.set.hash.THashSet;

import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
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

    static {
        CallType.initCache();
    }

    private final CallClientSocketFactory mSocketFactory;

    /**
     * All instantiated handlers. Allow closing of connections even if they
     * were not returned back to pool properly
     */
    private final Set<ClientHandler> mHandlers = new THashSet<ClientHandler>();
    /**
     * Currently available handlers
     */
    private final List<ClientHandler> mAvailable = new ArrayList<ClientHandler>();

    /**
     * Pending handlers to kill
     */
    private final Set<ClientHandler> mPending = new THashSet<ClientHandler>();

    
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
            final CallClientSocketFactory pSocketFactory,
            final boolean pCounterEnabled,
            final boolean pReuseObjectStream,
            final int pCallCompressThreshold) 
    {
        super(pServerAddress, pPort, pRegistry, pIOFactory);
        
        mSocketFactory = pSocketFactory != null
            ? pSocketFactory
            : DefaultCallClientSocketFactory.INSTANCE;
        
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
     * @throws RemoteException if cannot connect to server
     */
    public synchronized ClientHandler reserve() throws RemoteException {
        ClientHandler handler = null;
        
        while (handler == null && !mAvailable.isEmpty()) {
            handler = mAvailable.remove(mAvailable.size() - 1);
            if (!handler.isRunning()) {
                handler = null;
            }
        }
        
        if (handler == null) {
            try {
                Socket socket = mSocketFactory.createSocket(
                        getServerAddress(), 
                        getPort());
                
                handler = new ClientHandler(socket, this);
                mHandlers.add(handler);
            } catch (IOException e) {
                throw new RemoteException("Failed to connect server", e);
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
            mAvailable.add(pHandler);
        } else {
            // ensure socket is properly closed
            pHandler.kill();
            pHandler.free();
            mHandlers.remove(pHandler);
        }
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
        for (ClientHandler handler : mAvailable) {
            handler.kill();
            handler.free();
        }
        mHandlers.removeAll(mAvailable);
        mPending.removeAll(mAvailable);
        mAvailable.clear();

        if (pForceKill) {
            for (ClientHandler handler : mHandlers) {
                handler.kill();
            }
            for (ClientHandler handler : mPending) {
                handler.kill();
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
