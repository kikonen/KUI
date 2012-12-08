package org.kari.util;

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

    public byte[] getBuffer() {
        return buf;
    }
}
