package org.kari.call.io;

import java.io.IOException;
import java.io.InputStream;

/**
 * Count bytes read from pIn
 *
 * @author kari
 */
public final class CountInputStream extends InputStream {
    private final InputStream mIn;
	
    private long mCount;
    private long mMark;
    
    public CountInputStream(InputStream pIn) {
    	mIn = pIn;
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
        int value = mIn.read();
        if (value > 0) {
            mCount++;
        }
        return value;
    }

    @Override
    public int read(byte b[]) throws IOException {
        return read(b, 0, b.length);
    }

    @Override
    public int read(byte[] pB, int pOff, int pLen) throws IOException {
        int count = mIn.read(pB, pOff, pLen);
        if (count > 0) {
            mCount += count;
        }
        return count;
    }

    @Override
    public long skip(long n) throws IOException {
        long count = mIn.skip(n);
        if (count > 0) {
            mCount += count;
        }
        return count;
    }

    @Override
    public int available() throws IOException {
        return mIn.available();
    }

    @Override
    public void close() throws IOException {
        mIn.close();
    }

    @Override
    public void mark(int readlimit) {
        mIn.mark(readlimit);
    }

    @Override
    public void reset() throws IOException {
        mIn.reset();
    }

    @Override
    public boolean markSupported() {
        return mIn.markSupported();
    }
}
