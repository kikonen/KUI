package org.kari.base;

/**
 * Resource identifier base
 */
public final class RID {
    private final String mBase;

    public RID(String pBase) {
        mBase = pBase + ".";
    }

    public String getBase() {
        return mBase;
    }
    
    @Override
    public String toString() {
        return mBase;
    }

}
