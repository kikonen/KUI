package org.kari.call.io;

import java.io.ByteArrayInputStream;

/**
 * Byte stream, which allows resetting buffer
 *
 * @author kari
 */
public final class DirectByteArrayInputStream extends ByteArrayInputStream {
    private static final byte[] EMPTY_BUFFER = new byte[0];

    public DirectByteArrayInputStream(byte[] pBuf, int pOffset, int pLength) {
        super(pBuf, pOffset, pLength);
    }

    public DirectByteArrayInputStream(byte[] pBuf) {
        super(pBuf);
    }

    public DirectByteArrayInputStream() {
        super(EMPTY_BUFFER, 0, 0);
    }

    public void set(byte[] pBuf, int pOffset, int pLength) {
        buf = pBuf;
        pos = pOffset;
        count = Math.min(pOffset + pLength, buf.length);
        mark = pOffset;
    }
    
    public void empty() {
        set(EMPTY_BUFFER, 0, 0);
    }
    
    public byte[] getBuffer() {
        return buf;
    }
}
