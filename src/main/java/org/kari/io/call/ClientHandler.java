package org.kari.io.call;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.kari.io.CountInputStream;
import org.kari.io.CountOutputStream;

/**
 * Handles communication with one socket in client side
 *
 * @author kari
 */
public final class ClientHandler {
    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".client_handler");
    
    private final Socket mSocket;
    
    private final CountOutputStream mCountOut;
    private final CountInputStream mCountIn;
    
    private final DataInputStream mIn;
    private final DataOutputStream mOut;
    
    private volatile boolean mRunning = true;
    
    
    public ClientHandler(Socket pSocket) throws IOException {
        mSocket = pSocket;
        mCountOut = new CountOutputStream(mSocket.getOutputStream());
        mCountIn = new CountInputStream(mSocket.getInputStream());
        mIn = new DataInputStream(new BufferedInputStream(mCountIn));
        mOut = new DataOutputStream(new BufferedOutputStream(mCountOut));
    }
    
    public void kill() {
        mRunning = false;
        CallUtil.closeSocket(mSocket);
    }
    
    public boolean isAlive() {
        return mRunning && !mSocket.isClosed();
    }
    
    /**
     * Invoke call, retrying call is upto caller
     */
    public Object invoke(Call pCall) throws Throwable {
        Result result;

        mCountOut.markCount();
        mCountIn.markCount();
        try {
            pCall.send(mOut);
            int code = mIn.read();
            
            CallType type = CallType.resolve(code);
            result = (Result)type.create();
            
            result.receive(mIn);
        } catch (Throwable e) {
            // suicide; failed write/read socket or invalid result type received
            // thus input is already in invalid state
            // => However, should differentiate phase; if call was sent
            //    succesfully to server, then server may have actually handled it.
            //    In that case transparent "retry" is not possible
            kill();
            throw new RemoteException("Failed to access server", e);
        } finally {
            if (false) {
                LOG.info("out=" + mCountOut.getMarkSize() + ", in=" + mCountIn.getMarkSize());
            }
        }
        
        return result.getResult();
    }
    
}
