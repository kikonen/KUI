package org.kari.call;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.log4j.Logger;
import org.kari.call.io.CountInputStream;
import org.kari.call.io.CountOutputStream;
import org.kari.call.io.DirectByteArrayInputStream;
import org.kari.call.io.DirectByteArrayOutputStream;
import org.kari.call.io.IOFactory;

/**
 * Shared logic between server and client side for handlers
 *
 * @author kari
 */
public abstract class Handler {
    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".handler");
    private static final int BUFFER_SIZE = 8192;
    static final int COMPRESSION_LEVEL = Deflater.DEFAULT_COMPRESSION;
    static final boolean TRACE = false;

    
    protected final Socket mSocket;
    
    protected final boolean mCounterEnabled;
    protected final CountOutputStream mCountOut;
    protected final CountInputStream mCountIn;
    
    protected final DataInputStream mIn;
    protected final DataOutputStream mOut;

    protected final IOFactory mIOFactory;
    
    protected final TransferCounter mCounter;

    private DirectByteArrayOutputStream mByteOut;
    private DirectByteArrayInputStream mInBuffer;
    private byte[] mDataBuffer;
    
    private Deflater mDeflater;
    private Inflater mInflater;
    
    protected volatile boolean mRunning = true;

    
    protected Handler(
            Socket pSocket,
            IOFactory pIOFactory,
            boolean pCounterEnabled) 
        throws IOException 
    {
        mSocket = pSocket;
        
        mCounterEnabled = pCounterEnabled;
        if (pCounterEnabled) {
            mCountOut = new CountOutputStream(mSocket.getOutputStream());
            mCountIn = new CountInputStream(mSocket.getInputStream());
            mCounter = TransferCounter.INSTANCE;
            
            mIn = new DataInputStream(new BufferedInputStream(mCountIn));
            mOut = new DataOutputStream(new BufferedOutputStream(mCountOut));
        } else {
            mCountOut = null;
            mCountIn = null;
            mCounter = null;
            
            mIn = new DataInputStream(new BufferedInputStream(mSocket.getInputStream()));
            mOut = new DataOutputStream(new BufferedOutputStream(mSocket.getOutputStream()));
        }
        
        mIOFactory = pIOFactory;
    }
    
    public void kill() {
        mRunning = false;
        CallUtil.closeSocket(mSocket);
        free();
    }

    /**
     * Free resources
     */
    protected void free() {
        if (mInflater != null) {
            mInflater.end();
        }
        if (mDeflater != null) {
            mDeflater.end();
        }
    }
    
    public boolean isRunning() {
        return mRunning && !mSocket.isClosed();
    }
    
    public IOFactory getIOFactory() {
        return mIOFactory;
    }

    /**
     * Get reused encoding buffer
     * 
     * @see #getObjectOut()
     */
    public final DirectByteArrayOutputStream getByteOut() {
        if (mByteOut == null) {
            mByteOut = new DirectByteArrayOutputStream();
        }
        return mByteOut;
    }

    /**
     * Reset encoding buffer, if buffer is allocated
     */
    public final void resetByteOut() {
        if (mByteOut != null) {
            mByteOut.reset();
        }
    }

    /**
     * Prepare {@link #getByteOut()} for writing pCount data
     * 
     * @return direct reference to {@link #getByteOut()} buffer with
     * ensure pCount capasity
     */
    public final byte[] prepareByteOut(int pCount) {
        final DirectByteArrayOutputStream out = getByteOut();
    
        final byte[] data = getDataBuffer();
        while (out.getBuffer().length < pCount) {
            out.write(data, 0, data.length);
        }
        
        return out.getBuffer();
    }

    /**
     * Reused wrapper for {@link #getByteOut()} used when reading data
     * 
     * @see #getObjectIn()
     */
    private final DirectByteArrayInputStream getByteIn() {
        if (mInBuffer == null) {
            mInBuffer = new DirectByteArrayInputStream();
        }
        return mInBuffer;
    }

    /**
     * Get reused data buffer
     */
    public final byte[] getDataBuffer() {
        if (mDataBuffer == null) {
            mDataBuffer = new byte[BUFFER_SIZE];
        }
        return mDataBuffer;
    }

    public Deflater getDeflater() {
        if (mDeflater == null) {
            mDeflater = new Deflater(COMPRESSION_LEVEL, true);
        }
        return mDeflater;
    }

    public Inflater getInflater() {
        if (mInflater == null) {
            mInflater = new Inflater(true);
        }
        return mInflater; 
    }

    /**
     * @return ObjectOutput around {@link #getByteOut()}
     */
    public ObjectOutputStream createObjectOut()
        throws IOException 
    {
        return mIOFactory.createObjectOutput(getByteOut(), false);
    }
    
    /**
     * @return ObjectOutput around {@link #getByteIn()}
     */
    public ObjectInputStream createObjectIn(int pCount)
        throws IOException 
    {
        // wrap data in "write" buffer into "read" buffer
        DirectByteArrayInputStream bin = getByteIn();
        bin.reset();
        bin.set(mByteOut.getBuffer(), 0, pCount);

        return mIOFactory.createObjectInput(getByteIn(), false);
    }

}
