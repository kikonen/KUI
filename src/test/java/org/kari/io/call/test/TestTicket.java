package org.kari.io.call.test;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.rmi.dgc.VMID;

public final class TestTicket implements Externalizable {
    private long mTicket = 32434893298934423L;
    private VMID mId = new VMID();
    
    /**
     * For Externalizable
     */
    public TestTicket() {
        // nothing
    }
    
    @Override
    public void writeExternal(ObjectOutput pOut) throws IOException {
        pOut.writeLong(mTicket);
        pOut.writeObject(mId);
    }

    @Override
    public void readExternal(ObjectInput pIn) 
        throws IOException,
            ClassNotFoundException 
    {
        mTicket = pIn.readLong();
        mId = (VMID)pIn.readObject();
    }
    
    
}
