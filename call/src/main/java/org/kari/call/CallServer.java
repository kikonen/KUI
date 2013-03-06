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
public final class CallServer extends CallBase 
    implements Runnable
{
    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".server");

    static {
        CallType.initCache();
    }

    private final CallServerSocketFactory mSocketFactory;
    private final CallInvoker mCallInvoker;
    private ServerSocket mServer;
    private Thread mThread;
    
    /**
     * @param pServerAddress Either actual IP/host or application specific
     * identity for server addresses
     * @param pSocketFactory if null default (plain socket) is used
     * @param pIOFactory if null default is used
     * @param pRegistry If null new registry is created with default resolver
     * @param pCallInvoker null for default invoker
     * @param pCounterEnabled Is counter stats collected
     * @param pReuseObjectStream If true framework reuses Object IO streams
     * created via IOFactory
     */
    public CallServer(
            final String pServerAddress,
            final int pPort,
            final ServiceRegistry pRegistry,
            final IOFactory pIOFactory,
            final CallServerSocketFactory pSocketFactory,
            final CallInvoker pCallInvoker) 
    {
        super(pServerAddress, pPort, pRegistry, pIOFactory);
        
        mSocketFactory = pSocketFactory != null
            ? pSocketFactory
            : DefaultCallServerSocketFactory.INSTANCE;
        
        mCallInvoker = pCallInvoker != null 
            ? pCallInvoker 
            : DefaultCallInvoker.INSTANCE;
    }

    public CallInvoker getCallInvoker() {
        return mCallInvoker;
    }

    /**
     * @return true if server is not terminated
     */
    @Override
    public boolean isRunning() {
        return mRunning;
    }
    
    public synchronized void kill() {
        mRunning = false;
        mThread.interrupt();
        mThread = null;
        closeServerSocket();
    }

    /**
     * @param pDaemon If true server is started as daemon thread
     */
    public synchronized void start(boolean pDaemon) {
        if (mThread == null) {
            mThread = new Thread(this, "Server-" + getPort());
            mThread.setDaemon(pDaemon);
            mThread.start();
        }
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
            mServer = mSocketFactory.createSocket(
                    getServerAddress(),  
                    getPort());
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
