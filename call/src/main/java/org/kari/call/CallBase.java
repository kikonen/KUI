package org.kari.call;

import org.kari.call.event.BufferCall;
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

    public boolean isCounterEnabled() {
        return mCounterEnabled;
    }

    public void setCounterEnabled(boolean pCounterEnabled) {
        mCounterEnabled = pCounterEnabled;
    }

    public boolean isTraceTrafficStatistics() {
        return mTraceTrafficStatistics;
    }

    public void setTraceTrafficStatistics(boolean pTraceTrafficStatistics) {
        mTraceTrafficStatistics = pTraceTrafficStatistics;
    }

    public boolean isReuseObjectStream() {
        return mReuseObjectStream;
    }

    public void setReuseObjectStream(boolean pReuseObjectStream) {
        mReuseObjectStream = pReuseObjectStream;
    }

    /**
     * @return compress threshold for call (client) or result (server).
     */
    public int getCompressThreshold() {
        return mCompressThreshold;
    }

    /**
     * @param pResultCompressThreshold Threshold in bytes for result compression.
     * Use -1 to use default threshold, 0 for always and Integer.MAX_VALUE for never.
     */
    public void setCompressThreshold(int pCompressThreshold) {
        mCompressThreshold = pCompressThreshold;
        mCompressThreshold = pCompressThreshold < 0
            ? BufferCall.DEFAULT_COMPRESS_THRESHOLD
            : pCompressThreshold;
    }

    public boolean isRunning() {
        return mRunning;
    }

    public void setRunning(boolean pRunning) {
        mRunning = pRunning;
    }

    public String getServerAddress() {
        return mServerAddress;
    }

    public int getPort() {
        return mPort;
    }

    public IOFactory getIOFactory() {
        return mIOFactory;
    }

    public ServiceRegistry getRegistry() {
        return mRegistry;
    }

}
