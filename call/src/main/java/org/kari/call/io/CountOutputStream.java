package org.kari.call.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Count bytes written into pOut
 *
 * @author kari
 */
public final class CountOutputStream extends FilterOutputStream {
    private long mCount;
    private long mMark;
    
    public CountOutputStream(OutputStream pOut) {
        super(pOut);
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
    public void write(int pB) throws IOException {
        out.write(pB);
        mCount++;
    }

    @Override
    public void write(byte[] pB, int pOff, int pLen) throws IOException {
        out.write(pB, pOff, pLen);
        mCount += pLen;
    }

    
}
