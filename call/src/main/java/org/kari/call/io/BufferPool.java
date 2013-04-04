package org.kari.call.io;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.kari.call.CallConstants;

/**
 * Manages byte buffers for call API
 *
 * @author kari
 */
public class BufferPool {
    static final class Holder {
        public static final BufferPool INSTANCE = new BufferPool();
    }

    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".io.pool");

    /**
     * If JVM does gc() for this element, then it's handled as trigger to
     * flush whole buffer pool (i.e. JVM is low in memory).
     *
     * @author kari
     */
    static final class Flush {
        private final BufferPool mPool;

        Flush(BufferPool pPool) {
            mPool = pPool;
        }

        @Override
        protected void finalize() throws Throwable {
            mPool.clear();
        }
    }

    /**
     * List of buffers for one slot
     *
     * @author kari
     */
    static final class Slot {
        private final int mSize;
        private final int mMaxCount;
        private final List<byte[]> mBuffers = new ArrayList<byte[]>();

        Slot(int pSize, int pMaxCount) {
            mSize = pSize;
            mMaxCount = pMaxCount;
        }

        byte[] allocate() {
            byte[] buffer;

            if (!mBuffers.isEmpty()) {
                buffer = mBuffers.remove(mBuffers.size() - 1);
            } else {
                buffer = new byte[mSize];
            }

            return buffer;
        }

        void release(byte[] pBuffer) {
            if (mBuffers.size() < mMaxCount) {
                mBuffers.add(pBuffer);
            }
        }
    }

    private static final byte[] EMPTY_BUFFER = new byte[0];


    /**
     * Buffers above this range are not pooled; they should be rare peculiarity
     */
    public static final int MAX_SLOT_SIZE = 24;

    static {
        if (false) {
            int total;

            total = 0;
            for (int i = 0; i <= MAX_SLOT_SIZE; i++) {
                int v = MAX_SLOT_SIZE + 2 - i;
                int maxCount = (int)(Math.log(v)/Math.log10(v) * v);
                int size = getInstance().calculateSize(i);
                LOG.info("A: " + i +  "=> " + maxCount + ", size=" + size + ", total=" + (size * maxCount));
                total += size * maxCount;
            }
            LOG.info("A: totalSize=" + total);

            total = 0;
            for (int i = 0; i <= MAX_SLOT_SIZE; i++) {
                int v = MAX_SLOT_SIZE + 3 - i;
                int maxCount = (int)(Math.log10(v) * v);
                int size = getInstance().calculateSize(i);
                LOG.info("B: " + i +  "=> " + maxCount + ", size=" + size + ", total=" + (size * maxCount));
                total += size * maxCount;
            }
            LOG.info("B: totalSize=" + total);

            total = 0;
            for (int i = 0; i <= MAX_SLOT_SIZE; i++) {
                int v = MAX_SLOT_SIZE + 2 - i;
                int maxCount = (int)(Math.log(v) * v);
                int size = getInstance().calculateSize(i);
                LOG.info("C: " + i +  "=> " + maxCount + ", size=" + size + ", total=" + (size * maxCount));
                total += size * maxCount;
            }
            LOG.info("C: totalSize=" + total);
        }
    }

    /**
     * Map of (slot, buffers)
     */
    private final TIntObjectMap<Slot> mBuffers = new TIntObjectHashMap<Slot>();
    private SoftReference<Flush> mFlush;


    final int mMaxSlotSize;
    final int mMaxPooledBufferSize;


    public static BufferPool getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * @see #MAX_SLOT_SIZE
     */
    public BufferPool() {
        this(MAX_SLOT_SIZE);
    }

    public BufferPool(int pMaxSlotSize) {
        mFlush = new SoftReference<Flush>(new Flush(this));

        int idx = pMaxSlotSize;
        int size = 1;
        while (idx > 0) {
            size *= 2;
            idx--;
        }

        mMaxSlotSize = pMaxSlotSize;
        mMaxPooledBufferSize = size;
    }

    /**
     * Clear whole pool
     */
    public synchronized final void clear() {
        mBuffers.clear();
        mFlush = new SoftReference<Flush>(new Flush(this));
        LOG.debug("flush cache");
    }

    /**
     * Calculate byte size for slot pIdx
     */
    private final int calculateSize(int pSlot) {
        int size = 1;
        while (pSlot > 0) {
            size *= 2;
            pSlot--;
        }
        return size;
    }

    /**
     * Calculate slot for pSize
     */
    private final int calculateSlotIndex(final int pSize) {
        int slot = -1;
        int remaining = pSize;
        while (remaining > 0) {
            remaining >>= 1;
            slot++;
        }

        final int size = calculateSize(slot);
        if (size != pSize) {
            slot++;
        }

        return slot;
    }

    /**
     * Calculate max reserved buffers for pSlot
     *
     * @param pSlot Index of slot
     * @param pSize size of slot
     */
    protected int calculateMaxCount(int pSlot, int pSize) {
        int v = mMaxSlotSize + 3 - pSlot;
        int maxCount = (int)(Math.log(v) * v);
        if (maxCount <= 0) {
            maxCount = 1;
        }
        return maxCount;
    }

    private Slot getSlot(int pSize) {
        int idx = calculateSlotIndex(pSize);
        Slot slot = mBuffers.get(idx);
        if (slot == null) {
            int size = calculateSize(idx);
            slot = new Slot(
                    size,
                    calculateMaxCount(idx, pSize));
            mBuffers.put(idx, slot);
        }
        return slot;
    }

    /**
     * Allocate buffer, which is at least pSize
     */
    public synchronized final byte[] allocate(int pSize) {
        if (pSize <= 0) {
            return EMPTY_BUFFER;
        }
        if (pSize <= mMaxPooledBufferSize) {
            return getSlot(pSize).allocate();
        }
        return new byte[pSize];
    }

    /**
     * Grow allocated buffer, which is at least pSize. Old buffer is released
     * and data is copied into new reserved buffer.
     *
     * @return new allocated buffer, pBuffer if it was already big enough
     */
    public final byte[] grow(byte[] pBuffer, int pSize) {
        byte[] buffer = pBuffer;

        // no alloc if buffer is already big enough
        if (pBuffer.length < pSize) {
            buffer = allocate(pSize);
            System.arraycopy(pBuffer, 0, buffer, 0, pBuffer.length);
            release(pBuffer);
        }

        return buffer;
    }

    /**
     * Change pBuffer with another buffer of pSize. Data is not copied and
     * pSize can be smaller than size of pBuffer
     *
     * @return new allocated buffer, same buffer if pBuffer is exactly same
     * as pSize
     */
    public final byte[] change(byte[] pBuffer, int pSize) {
        byte[] buffer = pBuffer;

        // no alloc if proper size already
        if (pBuffer.length != pSize) {
            buffer = allocate(pSize);
            release(pBuffer);
        }

        return buffer;
    }

    /**
     * Allocate buffer, which is at least pSize
     */
    public synchronized final void release(byte[] pBuffer) {
        if (pBuffer.length > 0 && pBuffer.length <= mMaxPooledBufferSize) {
            getSlot(pBuffer.length).release(pBuffer);
        }
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        getInstance().allocate(22);
    }
}
