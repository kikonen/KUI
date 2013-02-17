package org.kari.io;

import java.io.ByteArrayOutputStream;

/**
 * Direct byte array output stream
 * 
 * @author kari
 */
public final class DirectByteArrayOutputStream
    extends ByteArrayOutputStream
{
    public DirectByteArrayOutputStream() {
        this(1024);
    }

    public DirectByteArrayOutputStream(int size) {
        super(size);
    }

    /**
     * Direct reference to buffer. Allows optimal non-copy access to data
     * and reusing buffer (with reset())
     * 
     * @see #size()
     * @see #reset()
     */
    public byte[] getBuffer() {
        return buf;
    }
}
