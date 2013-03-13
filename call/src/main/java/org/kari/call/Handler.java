package org.kari.call;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.log4j.Logger;
import org.kari.call.event.Call;
import org.kari.call.event.CallEvent;
import org.kari.call.event.Result;
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
    /**
     * Buffer size for socket IO
     */
    private static final int IO_BUFFER_SIZE = 4096;
    /**
     * Default buffer size for encode/decode of calls
     */
    private static final int CALL_BUFFER_SIZE = 8192;

    static final int COMPRESSION_LEVEL = Deflater.DEFAULT_COMPRESSION;
    static final byte MAGIC_VALUE = -2;

    static final EOFException EOF_EXCEPTION = new EOFException();

    static {
        EOF_EXCEPTION.setStackTrace(new StackTraceElement[0]);
    }

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
    protected final boolean mTraceTrafficStatistics;

    private DirectByteArrayOutputStream mByteOut;
    private final DirectByteArrayInputStream mByteIn = new DirectByteArrayInputStream();

    private byte[] mBuffer;

    private Deflater mDeflater;
    private Inflater mInflater;


    private ObjectOutputStream mByteObjectOut;
    private ObjectInputStream mByteObjectIn;

    protected volatile boolean mRunning = true;

    private Object mLastSessionId;


    protected Handler(
            final Socket pSocket,
            final IOFactory pIOFactory,
            final boolean pCounterEnabled,
            final boolean pTraceTrafficStatistics,
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

            mIn = new DataInputStream(new BufferedInputStream(mCountIn, IO_BUFFER_SIZE));
            mOut = new DataOutputStream(new BufferedOutputStream(mCountOut, IO_BUFFER_SIZE));
        } else {
            mCountOut = null;
            mCountIn = null;
            mCounter = null;

            mIn = new DataInputStream(new BufferedInputStream(mSocket.getInputStream(), IO_BUFFER_SIZE));
            mOut = new DataOutputStream(new BufferedOutputStream(mSocket.getOutputStream(), IO_BUFFER_SIZE));
        }

        mIOFactory = pIOFactory;
        mTraceTrafficStatistics = pTraceTrafficStatistics;
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
            mByteOut = new DirectByteArrayOutputStream(CALL_BUFFER_SIZE);
        }
        return mByteOut;
    }

    /**
     * Reset encoding buffer, if buffer is allocated
     */
    public final void resetByteOut() {
        if (mByteOut != null) {
            mByteOut.set(CALL_BUFFER_SIZE);
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
            mBuffer = new byte[CALL_BUFFER_SIZE];
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

    /**
     * Read single call from client
     */
    protected final Call readCall()
        throws IOException,
            RemoteException,
            Exception
    {
        Call call = (Call)readEvent();

        if (call.isSessionIdChanged()) {
            mLastSessionId = call.getSessionId();
        } else {
            call.setSessionId(mLastSessionId);
        }

        return call;
    }

    /**
     * Read single result event from sender (client/server)
     */
    protected final Result readResult()
        throws IOException,
            RemoteException,
            Exception
    {
        return (Result)readEvent();
    }

    /**
     * Read single event from socket
     *
     * @throws EOFException If stream is ended while waiting event
     */
    public final CallEvent readEvent()
        throws
            Exception
    {
        if (mCounterEnabled) {
            mCountIn.markCount();
        }

        try {
            int code = mIn.read();
            if (code < 0) {
                // EOF
                throw EOF_EXCEPTION;
            }

            CallType type = CallType.resolve(code);

            CallEvent event = type.create();
            event.receive(this, mIn);

            return event;
        } finally {
            if (mCounterEnabled) {
                long count = mCountIn.getMarkSize();
                if (mTraceTrafficStatistics) LOG.info("in=" + count);
                if (count > 0) {
                    mCounter.addIn(count);
                }
            }
            resetByteOut();
        }
    }

    public final void writeEvent(CallEvent pEvent)
        throws Exception
    {
        if (mCounterEnabled) {
            mCountOut.markCount();
        }

        try {
            pEvent.send(this, mOut);
        } finally {
            if (mCounterEnabled) {
                long count = mCountOut.getMarkSize();
                if (mTraceTrafficStatistics) LOG.info("out=" + count);
                if (count > 0) {
                    mCounter.addOut(count);
                }
            }
            resetByteOut();
        }
    }

}
