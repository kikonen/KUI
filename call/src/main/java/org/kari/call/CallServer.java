package org.kari.call;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.kari.call.io.CallServerSocketFactory;
import org.kari.call.io.DefaultCallServerSocketFactory;
import org.kari.call.io.IOFactory;

/**
 * Handles listening client connections
 */
public final class CallServer extends Thread {
    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".server");

    static {
        CallType.initCache();
    }

    private final String mServerAddress;
    private final int mPort;
    private final CallServerSocketFactory mSocketFactory;
    private final IOFactory mIOFactory;
    private final ServiceRegistry mRegistry;
    
    private final CallInvoker mCallInvoker;

    private final boolean mCounterEnabled;
    private final boolean mReuseObjectStream;
    
    private volatile boolean mRunning = true;
    private ServerSocket mServer;
    
    
    /**
     * @param pServerAddress Either actual IP/host or application specific
     * identity for server addresses
     * @param pSocketFactory if null default (plain socket) is used
     * @param pIOFactory if null default is used
     * @param pRegistry If null new registry is created with default resolver
     * @param pCallInvoker null for default invoker
     * @param pCounterEnabled Is counter stats collected
     */
    public CallServer(
            String pServerAddress,
            int pPort,
            CallServerSocketFactory pSocketFactory,
            IOFactory pIOFactory,
            ServiceRegistry pRegistry,
            CallInvoker pCallInvoker,
            boolean pCounterEnabled,
            final boolean pReuseObjectStream) 
    {
        super("Server-" + pPort);
        
        mServerAddress = pServerAddress;
        mPort = pPort;
        
        mSocketFactory = pSocketFactory != null
            ? pSocketFactory
            : DefaultCallServerSocketFactory.INSTANCE;
        
        mIOFactory = pIOFactory != null
            ? pIOFactory
            : DefaultIOFactory.INSTANCE;
        
        mRegistry = pRegistry != null
            ? pRegistry
            : new ServiceRegistry(null);
        
        mCallInvoker = pCallInvoker != null 
            ? pCallInvoker 
            : DefaultCallInvoker.INSTANCE;
        
        mCounterEnabled = pCounterEnabled;
        mReuseObjectStream = pReuseObjectStream;
    }

    public IOFactory getIOFactory() {
        return mIOFactory;
    }

    public ServiceRegistry getRegistry() {
        return mRegistry;
    }

    public CallInvoker getCallInvoker() {
        return mCallInvoker;
    }

    public boolean isCounterEnabled() {
        return mCounterEnabled;
    }

    public boolean isReuseObjectStream() {
        return mReuseObjectStream;
    }

    public int getPort() {
        return mPort;
    }


    public void kill() {
        mRunning = false;
        closeServerSocket();
    }

    /**
     * @return true if server is not terminated
     */
    public boolean isRunning() {
        return mRunning;
    }

    @Override
    public void run() {
        try {
            while (mRunning) {
                Socket socket = null;
                try {
                    ServerSocket server = openServerSocket();
                    socket = server.accept();
                } catch (Throwable e) {
                    // restart server socket
                    closeServerSocket();
                }
                
                if (socket != null) {
                    try {
                        createHandler(socket).start();
                    } catch (Throwable e) {
                        LOG.error("handler failed", e);
                        CallUtil.closeSocket(socket);
                    }
                }
            }
        } finally {
            closeServerSocket();
        }
    }

    /**
     * Create new handler for pSocket. Handler is not started yet.
     */
    public ServerHandler createHandler(Socket pSocket) throws IOException {
        return new ServerHandler(this, pSocket);
    }
    
    private synchronized ServerSocket openServerSocket() 
        throws IOException 
    {
        if (mServer == null) {
            mServer = mSocketFactory.createSocket(mServerAddress,  mPort);
        }
        return mServer;
    }

    /**
     * Closes server socket if it's currently existing. Effectively this allows 
     * restarting server, without killing it. For example, if server socket
     * is somehow "stuck" due to OS issues, or actual server IP identified
     * by server address has changed.
     */
    public synchronized void closeServerSocket() {
        if (mServer != null) {
            mServer = CallUtil.closeSocket(mServer);
        }
    }

}
