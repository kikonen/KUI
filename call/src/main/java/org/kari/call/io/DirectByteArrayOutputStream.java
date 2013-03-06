package org.kari.call.io;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * Direct byte array output stream
 * 
 * <p>NOTE: KI Several methods overridden to avoid synchronized
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
    
    private void ensureCapacity(int minCapacity) {
        // overflow-conscious code
        if (minCapacity - buf.length > 0)
            grow(minCapacity);
    }

    /**
     * <pTODO KI Use BufferPool for buffer allocation
     */
    private void grow(int minCapacity) {
        // overflow-conscious code
        int oldCapacity = buf.length;
        int newCapacity = oldCapacity << 1;
        if (newCapacity - minCapacity < 0)
            newCapacity = minCapacity;
        if (newCapacity < 0) {
            if (minCapacity < 0) // overflow
                throw new OutOfMemoryError();
            newCapacity = Integer.MAX_VALUE;
        }
        buf = Arrays.copyOf(buf, newCapacity);
    }

    /**
     * <p>NOTE: KI overridden to avoid synchronized
     */
    @Override
	public void write(int b) {
        ensureCapacity(count + 1);
        buf[count] = (byte) b;
        count += 1;
    }

    /**
     * <p>NOTE: KI overridden to avoid synchronized
     */
    @Override
	public void write(byte b[], int off, int len) {
        if ((off < 0) || (off > b.length) || (len < 0) ||
            ((off + len) - b.length > 0)) {
            throw new IndexOutOfBoundsException();
        }
        ensureCapacity(count + len);
        System.arraycopy(b, off, buf, count, len);
        count += len;
    }

    /**
     * <p>NOTE: KI overridden to avoid synchronized
     */
    @Override
	public void reset() {
        count = 0;
    }

    /**
     * <p>NOTE: KI overridden to avoid synchronized
     */
    @Override
	public int size() {
        return count;
    }

}
