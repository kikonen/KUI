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
    static final byte MAGIC_VALUE = -2;
    
    protected final Socket mSocket;
    
    protected final boolean mCounterEnabled;
    protected final CountOutputStream mCountOut;
    protected final CountInputStream mCountIn;
    protected final TransferCounter mCounter;
    
    protected final DataInputStream mIn;
    protected final DataOutputStream mOut;

    protected final IOFactory mIOFactory;
    
    private final boolean mReuseObjectStream;
    private final int mCompressThreshold;

    private DirectByteArrayOutputStream mByteOut;
    private DirectByteArrayInputStream mByteIn;
    private DirectByteArrayOutputStream mCompressBuffer;
    
    private byte[] mDataBuffer;
    
    private Deflater mDeflater;
    private Inflater mInflater;

    
    private ObjectOutputStream mByteObjectOut;
    private ObjectInputStream mByteObjectIn;
    
    protected volatile boolean mRunning = true;

    
    protected Handler(
            final Socket pSocket,
            final IOFactory pIOFactory,
            final boolean pCounterEnabled,
            final boolean pReuseObjectStream,
            final int pCompressThreshold) 
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
        mReuseObjectStream = pReuseObjectStream;
        
        mCompressThreshold = pCompressThreshold;
    }
    
    public void kill() {
        mRunning = false;
        CallUtil.closeSocket(mSocket);
        free();
    }

    /**
     * Free resources
     */
    protected final void free() {
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
    
    public final IOFactory getIOFactory() {
        return mIOFactory;
    }

    public final boolean isReuseObjectStream() {
        return mReuseObjectStream;
    }

    /**
     * Get call (client side) or result (server side) compress threshold
     */
    public final int getCompressThreshold() {
        return mCompressThreshold;
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
     * Temp deflate/inflate buffer
     */
    public final DirectByteArrayOutputStream getCompressBuffer() {
        if (mCompressBuffer == null) {
            mCompressBuffer = new DirectByteArrayOutputStream();
        }
        return mCompressBuffer;
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
        if (mByteIn == null) {
            mByteIn = new DirectByteArrayInputStream();
        }
        return mByteIn;
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

    public final Deflater getDeflater() {
        if (mDeflater == null) {
            mDeflater = new Deflater(COMPRESSION_LEVEL, true);
        }
        return mDeflater;
    }

    public final Inflater getInflater() {
        if (mInflater == null) {
            mInflater = new Inflater(true);
        }
        return mInflater; 
    }

    /**
     * @return ObjectOutput around {@link #getByteOut()}
     */
    public final ObjectOutputStream createObjectOut()
        throws IOException 
    {
        return mReuseObjectStream 
            ? getByteObjectOut()
            : mIOFactory.createObjectOutput(getByteOut(), false);
    }
    
    /**
     * @return ObjectOutput around {@link #getByteIn()}
     */
    public final ObjectInputStream createObjectIn(int pCount)
        throws IOException 
    {
        // wrap data in "write" buffer into "read" buffer
        DirectByteArrayInputStream bin = getByteIn();
        bin.reset();
        bin.set(mByteOut.getBuffer(), 0, pCount);

        return mReuseObjectStream
            ? getByteObjectIn()
            : mIOFactory.createObjectInput(getByteIn(), false);
    }

    /**
     * Reused stream bound into {@link #getByteOut()}
     */
    public final ObjectOutputStream getByteObjectOut() 
        throws IOException
    {
        if (mByteObjectOut == null) {
            mByteObjectOut = mIOFactory.createObjectOutput(getByteOut(), false);
        }
        return mByteObjectOut;
    }

    /**
     * Reused stream bound into {@link #getByteIn()}
     */
    public final ObjectInputStream getByteObjectIn() 
        throws IOException
    {
        if (mByteObjectIn == null) {
            mByteObjectIn = mIOFactory.createObjectInput(getByteIn(), false);
        }
        return mByteObjectIn;
    }

    public final void finishObjectOut(ObjectOutputStream pOut)
        throws IOException 
    {
        if (mReuseObjectStream) {
            pOut.reset();
            // reset won't work unless there is some data after it
            pOut.writeByte(MAGIC_VALUE);
        }
        pOut.flush();
    }
    
    public final void finishObjectIn(ObjectInputStream pIn)
        throws IOException 
    {
        // consume TC_RESET
        if (mReuseObjectStream) {
            // consume MAGIC_VALUE from stream
            byte data = pIn.readByte();
            if (data != MAGIC_VALUE) {
                throw new IOException("corrupted stream");
            }
        }
    }

}
