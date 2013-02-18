package org.kari.call;

import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.kari.call.io.CallClientSocketFactory;
import org.kari.call.io.IOFactory;

/**
 * Handles making connections to server
 *
 * @author kari
 */
public final class CallClient {
    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".client");

    
    private final String mServerAddress;
    private final int mPort;
    private final CallClientSocketFactory mSocketFactory;
    private final IOFactory mIOFactory;
    private final ServiceRegistry mRegistry;
    
    private final List<ClientHandler> mPool = new ArrayList<ClientHandler>();
    
    public CallClient(
            String pServerAddress, 
            int pPort,
            CallClientSocketFactory pSocketFactory,
            IOFactory pIOFactory,
            ServiceRegistry pRegistry) 
    {
        mServerAddress = pServerAddress;
        mPort = pPort;
        mSocketFactory = pSocketFactory;
        mIOFactory = pIOFactory;
        
        mRegistry = pRegistry;
    }

    public String getServerAddress() {
        return mServerAddress;
    }

    public int getPort() {
        return mPort;
    }

    public ServiceRegistry getRegistry() {
        return mRegistry;
    }

    public IOFactory getIOFactory() {
        return mIOFactory;
    }

    /**
     * Get available handler from pool of handlers or create new handler
     * 
     * @throws IOException if cannot connect to server
     */
    public synchronized ClientHandler reserve() throws RemoteException {
        ClientHandler handler = null;
        
        while (handler == null && !mPool.isEmpty()) {
            handler = mPool.remove(mPool.size() - 1);
            if (!handler.isRunning()) {
                handler = null;
            }
        }
        
        if (handler == null) {
            try {
                Socket socket = mSocketFactory.createSocket(mServerAddress, mPort);
                handler = new ClientHandler(socket, this);
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
        if (pHandler.isRunning()) {
            mPool.add(pHandler);
        } else {
            // ensure socket is properly closed
            pHandler.kill();
        }
    }

}
