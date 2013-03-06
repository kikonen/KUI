package org.kari.call.io;

import java.io.ByteArrayInputStream;

/**
 * Byte stream, which allows resetting buffer
 * 
 * <p>NOTE: KI Several methods overridden to avoid synchronized
 * 
 * @author kari
 */
public final class DirectByteArrayInputStream extends ByteArrayInputStream {
    private static final byte[] EMPTY_BUFFER = new byte[0];

    public DirectByteArrayInputStream() {
        super(EMPTY_BUFFER, 0, 0);
    }

    /**
     * Allow resetting internal buffer into another one. Useful in case this 
     * input stream is source some other reused input stream.
     */
    public void set(byte[] pBuf, int pOffset, int pLength) {
        byte[] old = buf;
        
        buf = pBuf;
        pos = pOffset;
        count = Math.min(pOffset + pLength, buf.length);
        mark = pOffset;

        // NOTE KI can't release buffer; it may be owned by another location
        // ex. DirectByteArrayOutputStream
    }
    
    /**
     * Changes buffer into "byte[0]" buffer
     */
    public void empty() {
        set(EMPTY_BUFFER, 0, 0);
    }

    /**
     * @return Direct reference to buffer
     */
    public byte[] getBuffer() {
        return buf;
    }
    
    /**
     * <p>NOTE: KI overridden to avoid synchronized
     */
    @Override
	public int read() {
        return (pos < count) ? (buf[pos++] & 0xff) : -1;
    }

    /**
     * <p>NOTE: KI overridden to avoid synchronized
     */
    @Override
	public int read(byte b[], int off, int len) {
        if (b == null) {
            throw new NullPointerException();
        } else if (off < 0 || len < 0 || len > b.length - off) {
            throw new IndexOutOfBoundsException();
        }

        if (pos >= count) {
            return -1;
        }

        int avail = count - pos;
        if (len > avail) {
            len = avail;
        }
        if (len <= 0) {
            return 0;
        }
        System.arraycopy(buf, pos, b, off, len);
        pos += len;
        return len;
    }

    /**
     * <p>NOTE: KI overridden to avoid synchronized
     */
    @Override
	public long skip(long n) {
        long k = count - pos;
        if (n < k) {
            k = n < 0 ? 0 : n;
        }

        pos += k;
        return k;
    }

    /**
     * <p>NOTE: KI overridden to avoid synchronized
     */
    @Override
	public int available() {
        return count - pos;
    }

    /**
     * <p>NOTE: KI overridden to avoid synchronized
     */
    @Override
	public void reset() {
        pos = mark;
    }

}
