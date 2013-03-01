package org.kari.call;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.kari.call.io.CountInputStream;
import org.kari.call.io.CountOutputStream;
import org.kari.call.io.DirectByteArrayOutputStream;
import org.kari.call.io.IOFactory;

/**
 * Shared logic between server and client side for handlers
 *
 * @author kari
 */
public abstract class Handler {
    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".handler");
    static final boolean TRACE = false;

    protected final Socket mSocket;
    
    protected final CountOutputStream mCountOut;
    protected final CountInputStream mCountIn;
    
    protected final DataInputStream mIn;
    protected final DataOutputStream mOut;

    protected final IOFactory mIOFactory;
    
    protected final TransferCounter mCounter;

    private DirectByteArrayOutputStream mBuffer;
    private byte[] mDataBuffer;

    protected volatile boolean mRunning = true;

    
    protected Handler(
            Socket pSocket,
            IOFactory pIOFactory) 
        throws IOException 
    {
        mSocket = pSocket;
        mCountOut = new CountOutputStream(mSocket.getOutputStream());
        mCountIn = new CountInputStream(mSocket.getInputStream());
        mIn = new DataInputStream(new BufferedInputStream(mCountIn));
        mOut = new DataOutputStream(new BufferedOutputStream(mCountOut));
        
        mIOFactory = pIOFactory;
        mCounter = TransferCounter.INSTANCE;
    }
    
    public void kill() {
        mRunning = false;
        CallUtil.closeSocket(mSocket);
    }
    
    public boolean isRunning() {
        return mRunning && !mSocket.isClosed();
    }
    
    public IOFactory getIOFactory() {
        return mIOFactory;
    }

    /**
     * Get reused encoding buffer
     */
    public final DirectByteArrayOutputStream getBuffer() {
        if (mBuffer == null) {
            mBuffer = new DirectByteArrayOutputStream();
        }
        return mBuffer;
    }

    /**
     * Reset encoding buffer, if buffer is allocated
     */
    public final void resetBuffer() {
        if (mBuffer != null) {
            mBuffer.reset();
        }
    }

    /**
     * Get reused data buffer
     */
    public final byte[] getDataBuffer() {
        if (mDataBuffer == null) {
            mDataBuffer = new byte[4096];
        }
        return mDataBuffer;
    }


}
