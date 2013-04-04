package org.kari.call.io;

import java.io.ByteArrayOutputStream;

/**
 * Direct byte array output stream
 *
 * <p>NOTE KI Several methods overridden to avoid synchronized
 * <p>NOTE KI Uses internally BufferPool for memory allocation to reduce gc()
 *
 * @author kari
 */
public final class DirectByteArrayOutputStream
    extends ByteArrayOutputStream
{
    private final BufferPool mPool;

    public DirectByteArrayOutputStream() {
        this(BufferPool.getInstance(), 1024);
    }

    public DirectByteArrayOutputStream(int pSize) {
        this(BufferPool.getInstance(), pSize);
    }

    public DirectByteArrayOutputStream(BufferPool pPool) {
        this(pPool, 1024);
    }

    public DirectByteArrayOutputStream(BufferPool pPool, int pSize) {
        super(0);
        mPool = pPool;
        buf = mPool.allocate(pSize);
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

    /**
     * Reset internal buffer into buffer of size pSize (or minimal pooled
     * size, which is at least pSize)
     */
    public void set(int pSize) {
        buf = mPool.change(buf, pSize);
    }

    public void ensureCapacity(int minCapacity) {
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
            if (minCapacity < 0) {
                // overflow
                throw new OutOfMemoryError();
            }
            newCapacity = Integer.MAX_VALUE;
        }

        byte[] newBuffer = mPool.allocate(newCapacity);
        // copy only existing data
        System.arraycopy(buf, 0, newBuffer, 0, count);
        mPool.release(buf);
        buf = newBuffer;
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
