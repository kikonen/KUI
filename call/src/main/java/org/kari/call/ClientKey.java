package org.kari.call;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.UUID;


/**
 * UUID identifying specific client
 *
 * @author kari
 */
public final class ClientKey implements Externalizable {
    private long mMost;
    private long mLeast;

    /**
     * New UUID
     */
    public ClientKey() {
        UUID uuid = UUID.randomUUID();
        mMost = uuid.getMostSignificantBits();
        mLeast = uuid.getLeastSignificantBits();
    }

    @Override
    public int hashCode() {
        return (int)(mMost ^ mLeast);
    }

    @Override
    public boolean equals(Object pObj) {
        return pObj != null
            && pObj.getClass() == getClass()
            && ((ClientKey)pObj).mMost == mMost
            && ((ClientKey)pObj).mLeast == mLeast;
    }

    @Override
    public void writeExternal(ObjectOutput pOut) throws IOException {
        pOut.writeLong(mMost);
        pOut.writeLong(mLeast);
    }

    @Override
    public void readExternal(ObjectInput pIn)
        throws IOException,
            ClassNotFoundException
    {
        mMost = pIn.readLong();
        mLeast = pIn.readLong();
    }

}
