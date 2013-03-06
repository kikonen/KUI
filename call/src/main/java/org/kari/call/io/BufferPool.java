package org.kari.call.io;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages byte buffers for call API
 *
 * @author kari
 */
public final class BufferPool {
    /**
     * List of buffers for one slot
     *
     * @author kari
     */
    static final class Slot {
        private final int mIndex;
        private final int mSize;
        private final int mMaxCount;
        private final List<byte[]> mBuffers = new ArrayList<byte[]>();
        
        Slot(int pIndex) {
            mIndex = pIndex;
            mMaxCount = MAX_SLOT_SIZE  + 1 - pIndex;
            
            int idx = pIndex;
            int size = 1;
            while (idx > 0) {
                size *= 2;
                idx--;
            }
            mSize = size;

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
    
    private static final BufferPool INSTANCE = new BufferPool();
    private static final byte[] EMPTY_BUFFER = new byte[0];


    /**
     * pools are in multiples of blocksize
     */
    public static final int BLOCK_SIZE = 8192;
    
    /**
     * Buffers above this range are not pooled; they should be rare peculiarity
     */
    public static final int MAX_SLOT_SIZE = 24;
    public static final int MAX_POOLED_BUFFER_SIZE;
    
    static {
        int idx = MAX_SLOT_SIZE;
        int size = 1;
        while (idx > 0) {
            size *= 2;
            idx--;
        }
        MAX_POOLED_BUFFER_SIZE = size;
    }
    
    /**
     * Map of (slot, buffers)
     */
    private final TIntObjectMap<Slot> mBuffers = new TIntObjectHashMap<Slot>();
    
    
    public static BufferPool getInstance() {
        return INSTANCE;
    }

    private int calculateSlotIndex(int pSize) {
        int slot = 0;
        while (pSize > 0) {
            pSize >>= 1;
            slot++;
        }
        return slot;
    }
    
    private Slot getSlot(int pSize) {
        int idx = calculateSlotIndex(pSize);
        Slot slot = mBuffers.get(idx);
        if (slot == null) {
            slot = new Slot(idx);
            mBuffers.put(idx, slot);
        }
        return slot;
    }

    /**
     * Allocate buffer, which is at least pSize
     */
    public synchronized byte[] allocate(int pSize) {
        if (pSize <= 0) {
            return EMPTY_BUFFER;
        }
        if (pSize <= MAX_POOLED_BUFFER_SIZE) {
            return getSlot(pSize).allocate();
        }
        return new byte[pSize];
    }

    /**
     * Grow allocated buffer, which is at least pSize. Old buffer is released
     * and data is copied into new reserved buffer
     */
    public synchronized byte[] grow(byte[] pBuffer, int pSize) {
        byte[] buffer = allocate(pSize);
        System.arraycopy(pBuffer, 0, buffer, 0, pBuffer.length);
        release(pBuffer);
        return buffer;
    }

    /**
     * Allocate buffer, which is at least pSize
     */
    public synchronized void release(byte[] pBuffer) {
        if (pBuffer.length > 0 && pBuffer.length <= MAX_POOLED_BUFFER_SIZE) {
            getSlot(pBuffer.length).release(pBuffer);
        }
    }

}
