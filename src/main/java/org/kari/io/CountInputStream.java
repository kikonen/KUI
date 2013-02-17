package org.kari.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Count bytes read from pIn
 *
 * @author kari
 */
public final class CountInputStream extends FilterInputStream {
    private long mCount;
    private long mMark;
    
    public CountInputStream(InputStream pIn) {
        super(pIn);
    }

    public long getCount() {
        return mCount;
    }

    /**
     * Mark current count
     */
    public void markCount() {
        mMark = mCount;
    }
    
    public void resetCount() {
        mCount = 0;
        mMark = 0;
    }
    
    /**
     * @return diff between latest mark and count
     */
    public long getMarkSize() {
        return mCount - mMark;
    }

    @Override
    public int read() throws IOException {
        int value = in.read();
        mCount++;
        return value;
    }

    @Override
    public int read(byte[] pB, int pOff, int pLen) throws IOException {
        int count = in.read(pB, pOff, pLen);
        mCount += count;
        return count;
    }

    
}
