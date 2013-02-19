package org.kari.call;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.kari.call.io.CallServerSocketFactory;
import org.kari.call.io.IOFactory;

/**
 * Handles listening client connections
 */
public final class CallServer extends Thread {
    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".server");

    private final String mServerAddress;
    private final int mPort;
    private final CallServerSocketFactory mSocketFactory;
    private final IOFactory mIOFactory;
    private final ServiceRegistry mRegistry;
    
    private final CallInvoker mCallInvoker;

    
    private volatile boolean mRunning = true;
    private ServerSocket mServer;
    
    
    /**
     * @param pCallInvoker null for default invoker
     */
    public CallServer(
            String pServerAddress,
            int pPort,
            CallServerSocketFactory pSocketFactory,
            IOFactory pIOFactory,
            ServiceRegistry pRegistry,
            CallInvoker pCallInvoker) 
    {
        super("Server-" + pPort);
        
        mServerAddress = pServerAddress;
        mPort = pPort;
        mSocketFactory = pSocketFactory;
        mIOFactory = pIOFactory;
        
        mRegistry = pRegistry;
        mCallInvoker = pCallInvoker != null 
            ? pCallInvoker 
            : DefaultCallInvoker.INSTANCE;
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
                        new ServerHandler(this, socket).start();
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
    
    private synchronized ServerSocket openServerSocket() 
        throws IOException 
    {
        if (mServer == null) {
            mServer = mSocketFactory.createSocket(mServerAddress,  mPort);
        }
        return mServer;
    }

    private synchronized void closeServerSocket() {
        if (mServer != null) {
            mServer = CallUtil.closeSocket(mServer);
        }
    }

}
