package org.kari.call;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Collect global statistics of transferred data for (in/out) for both
 * client/server handlers in this JVM
 *
 * @author kari
 */
public final class TransferCounter {
    static final TransferCounter INSTANCE = new TransferCounter();
    
    
    private final AtomicLong mOutBytes = new AtomicLong();
    private final AtomicLong mInBytes = new AtomicLong();
    private final AtomicLong mCalls = new AtomicLong();


    public static TransferCounter getInstance() {
        return INSTANCE;
    }

    public void add(long pOutBytes, long pInBytes) {
        mOutBytes.addAndGet(pOutBytes);
        mInBytes.addAndGet(pInBytes);
        mCalls.incrementAndGet();
    }
    
    public long getOutBytes() {
        return mOutBytes.get();
    }
    
    public long getInBytes() {
        return mInBytes.get();
    }
    
    public long getCalls() {
        return mCalls.get();
    }
}

