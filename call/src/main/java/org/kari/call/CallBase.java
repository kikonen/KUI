package org.kari.call;

import org.kari.call.event.BufferCall;
import org.kari.call.io.BufferPool;
import org.kari.call.io.IOFactory;

/**
 * Shared logic between CallClient and CallServer
 *
 * @see CallClient
 * @see CallServer
 *
 * @author kari
 */
public abstract class CallBase {
    private final String mServerAddress;
    private final int mPort;
    private final IOFactory mIOFactory;
    private final ServiceRegistry mRegistry;

    private boolean mCounterEnabled;
    private boolean mTraceTrafficStatistics;
    private boolean mReuseObjectStream;
    private int mCompressThreshold;

    private BufferPool mBufferPool;

    private int mCallTimeout = CallConstants.CALL_TIMEOUT;

    private int mIdleTimeout;

    protected volatile boolean mRunning = true;


    protected CallBase(
            String pServerAddress,
            int pPort,
            ServiceRegistry pRegistry,
            IOFactory pIOFactory)
    {
        mServerAddress = pServerAddress;
        mPort = pPort;

        mRegistry = pRegistry != null
            ? pRegistry
            : new ServiceRegistry(null);

        mIOFactory = pIOFactory != null
            ? pIOFactory
            : DefaultIOFactory.INSTANCE;
    }

    /**
     * @return Call SO socket timeout in millis
     *
     * @see CallConstants#CALL_TIMEOUT
     */
    public final int getCallTimeout() {
        return mCallTimeout;
    }

    /**
     * <p>NOTE KI Since "event" code reading from socket uses retry in SOtimeout
     * case, extremely large timeouts shouldn't be needed
     *
     * @param pCallTimeout If <= 0 then not used but one set by socket factory
     * is utilized
     */
    public final void setCallTimeout(int pCallTimeout) {
        mCallTimeout = pCallTimeout;
    }

    /**
     * Idle cleanup time for connections.
     *
     * <p>NOTE KI For {@link ServerHandler} this defines also max SOtimeout,
     * to allow idle cleanup of server socket
     *
     * @return idle timeout in millis, 0 <= for never
     */
    public final int getIdleTimeout() {
        return mIdleTimeout;
    }

    /**
     * @param pIdleTimeout idle timeout in millis, 0 <=  for never
     */
    public void setIdleTimeout(int pIdleTimeout) {
        mIdleTimeout = pIdleTimeout;
    }

    public final boolean isCounterEnabled() {
        return mCounterEnabled;
    }

    public final void setCounterEnabled(boolean pCounterEnabled) {
        mCounterEnabled = pCounterEnabled;
    }

    public final boolean isTraceTrafficStatistics() {
        return mTraceTrafficStatistics;
    }

    public final void setTraceTrafficStatistics(boolean pTraceTrafficStatistics) {
        mTraceTrafficStatistics = pTraceTrafficStatistics;
    }

    public final boolean isReuseObjectStream() {
        return mReuseObjectStream;
    }

    public final void setReuseObjectStream(boolean pReuseObjectStream) {
        mReuseObjectStream = pReuseObjectStream;
    }

    /**
     * @return compress threshold for call (client) or result (server).
     */
    public final int getCompressThreshold() {
        return mCompressThreshold;
    }

    /**
     * @param pResultCompressThreshold Threshold in bytes for result compression.
     * Use -1 to use default threshold, 0 for always and Integer.MAX_VALUE for never.
     */
    public final void setCompressThreshold(int pCompressThreshold) {
        mCompressThreshold = pCompressThreshold;
        mCompressThreshold = pCompressThreshold < 0
            ? BufferCall.DEFAULT_COMPRESS_THRESHOLD
            : pCompressThreshold;
    }

    /**
     * @return null for default
     */
    public BufferPool getBufferPool() {
        return mBufferPool;
    }

    /**
     * Set buffer pool used for call API
     *
     * @param pBufferPool null for default
     */
    public void setBufferPool(BufferPool pBufferPool) {
        mBufferPool = pBufferPool;
    }

    public boolean isRunning() {
        return mRunning;
    }

    public void setRunning(boolean pRunning) {
        mRunning = pRunning;
    }

    public final String getServerAddress() {
        return mServerAddress;
    }

    public final int getPort() {
        return mPort;
    }

    public final IOFactory getIOFactory() {
        return mIOFactory;
    }

    public final ServiceRegistry getRegistry() {
        return mRegistry;
    }

}
