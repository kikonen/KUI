package org.kari.call;


/**
 * Collect global statistics of transferred data for (in/out) for both
 * client/server handlers in this JVM
 *
 * @author kari
 */
public final class TransferCounter {
    static final TransferCounter INSTANCE = new TransferCounter();


    private long mOutBytes;
    private long mInBytes;
    private long mInEvents;
    private long mOutEvents;


    public static TransferCounter getInstance() {
        return INSTANCE;
    }

    public synchronized void addIn(long pInBytes) {
        mInBytes += pInBytes;
        mInEvents++;
    }

    public synchronized void addOut(long pOutBytes) {
        mOutBytes += pOutBytes;
        mOutEvents++;
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
    public long getInEvents() {
        return mInEvents;
    }

    /**
     * <p>MUST sync to this externally
     */
    public long getOutEvents() {
        return mOutEvents;
    }
}

