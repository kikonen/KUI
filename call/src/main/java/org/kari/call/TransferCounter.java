package org.kari.call;


/**
 * Collect global statistics of transferred data for (in/out) for both
 * client/server handlers in this JVM
 *
 * @author kari
 */
public final class TransferCounter {
    public static final TransferCounter INSTANCE = new TransferCounter();


    private long mOutBytes;
    private long mInBytes;
    private long mCalls;


    public static TransferCounter getInstance() {
        return INSTANCE;
    }

    public synchronized void add(long pOutBytes, long pInBytes) {
        mOutBytes += pOutBytes;
        mInBytes += pInBytes;
        mCalls++;
    }

    /**
     * <p>MUST sync to this externally
     */
    public long getOutBytes() {
        return mOutBytes;
    }

    /**
     * <p>MUST sync to this externally
     */
    public long getInBytes() {
        return mInBytes;
    }

    /**
     * <p>MUST sync to this externally
     */
    public long getCalls() {
        return mCalls;
    }
}

