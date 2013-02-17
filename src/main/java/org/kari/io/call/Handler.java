package org.kari.io.call;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.kari.io.CountInputStream;
import org.kari.io.CountOutputStream;
import org.kari.io.DirectByteArrayOutputStream;

/**
 * Shared logic between server and client side for handlers
 *
 * @author kari
 */
public abstract class Handler {
    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".handler");

    protected final Socket mSocket;
    
    protected final CountOutputStream mCountOut;
    protected final CountInputStream mCountIn;
    
    protected final DataInputStream mIn;
    protected final DataOutputStream mOut;

    private DirectByteArrayOutputStream mBuffer;
    private byte[] mDataBuffer;

    protected volatile boolean mRunning = true;

    
    protected Handler(Socket pSocket) throws IOException {
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
    
    public boolean isRunning() {
        return mRunning && !mSocket.isClosed();
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
