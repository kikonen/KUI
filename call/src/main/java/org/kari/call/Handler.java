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
    static final byte MAGIC_VALUE = -2;

    protected final Socket mSocket;
    protected final int mSocketSOTimeout;
    protected final int mIdleTimeout;

    protected final boolean mCounterEnabled;
    protected final CountOutputStream mCountOut;
    protected final CountInputStream mCountIn;
    protected final TransferCounter mCounter;

    protected final DataInputStream mIn;
    protected final DataOutputStream mOut;

    protected final IOFactory mIOFactory;

    private final boolean mReuseObjectStream;
    private final int mCompressThreshold;
    protected final boolean mTraceTrafficStatistics;

    private DirectByteArrayOutputStream mByteOut;
    private final DirectByteArrayInputStream mByteIn = new DirectByteArrayInputStream();

    private byte[] mBuffer;

    private Deflater mDeflater;
    private Inflater mInflater;


    private ObjectOutputStream mByteObjectOut;
    private ObjectInputStream mByteObjectIn;

    private long mLastAcccessTime;

    protected volatile boolean mRunning = true;


    protected Handler(
            final Socket pSocket,
            CallBase pCall)
        throws IOException
    {
        mSocket = pSocket;
        mIdleTimeout = pCall.getIdleTimeout();

        int timeout = pCall.getCallTimeout();
        if (timeout > 0) {
            mSocket.setSoTimeout(timeout);
        } else {
            timeout = mSocket.getSoTimeout();
        }
        if (mIdleTimeout > 0) {
            timeout = Math.min(timeout,  mIdleTimeout);
        }
        mSocketSOTimeout = timeout;

        if (mSocketSOTimeout > 0) {
            mSocket.setSoTimeout(mSocketSOTimeout);
        }

        mCounterEnabled = pCall.isCounterEnabled();
        if (mCounterEnabled) {
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

        mIOFactory = pCall.getIOFactory();
        mTraceTrafficStatistics = pCall.isTraceTrafficStatistics();
        mReuseObjectStream = pCall.isReuseObjectStream();
        mCompressThreshold = pCall.getCompressThreshold();
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


    /**
     * Last access time for idle cleanup
     */
    public final long getLastAcccessTime() {
        return mLastAcccessTime;
    }

    public final void setLastAcccessTime(long pLastAcccessTime) {
        mLastAcccessTime = pLastAcccessTime;
    }

    /**
     * @return true if handler is idling
     */
    public boolean isIdling() {
        long diff = System.currentTimeMillis() - mLastAcccessTime;
        return diff >= mIdleTimeout;
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
            mByteOut = new DirectByteArrayOutputStream(BUFFER_SIZE);
        }
        return mByteOut;
    }

    /**
     * Reset encoding buffer, if buffer is allocated
     */
    public final void resetByteOut() {
        if (mByteOut != null) {
            mByteOut.set(BUFFER_SIZE);
        }

        if (mByteIn != null) {
            mByteIn.empty();
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

        out.reset();
        out.ensureCapacity(pCount);

        return out.getBuffer();
    }

    /**
     * Get reused data buffer for read/write/etc.
     */
    public final byte[] getBuffer() {
        if (mBuffer == null) {
            mBuffer = new byte[BUFFER_SIZE];
        }
        return mBuffer;
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
            : mIOFactory.createObjectOutput(getByteOut());
    }

    /**
     * Reused stream bound into {@link #mByteIn}
     */
    private final ObjectInputStream getByteObjectIn()
        throws IOException
    {
        if (mByteObjectIn == null) {
            mByteObjectIn = mIOFactory.createObjectInput(mByteIn);
        }
        return mByteObjectIn;
    }

    /**
     * @return input wrapper around {@link #getByteOut()} buffer
     */
    public final ObjectInputStream createObjectIn(int pSize)
        throws IOException
    {
        // wrap data in "write" buffer into "read" buffer
        DirectByteArrayInputStream bin = mByteIn;
        bin.reset();
        bin.set(getByteOut().getBuffer(), 0, pSize);

        return mReuseObjectStream
            ? getByteObjectIn()
            : mIOFactory.createObjectInput(mByteIn);
    }

    /**
     * Reused stream bound into {@link #getByteOut()}
     */
    public final ObjectOutputStream getByteObjectOut()
        throws IOException
    {
        if (mByteObjectOut == null) {
            mByteObjectOut = mIOFactory.createObjectOutput(getByteOut());
        }
        return mByteObjectOut;
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
