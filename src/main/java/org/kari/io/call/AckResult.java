package org.kari.io.call;

import java.io.DataInputStream;
import java.io.DataOutputStream;

/**
 * Different acks
 *
 * @author kari
 */
public abstract class AckResult extends Result {
    public AckResult(CallType pAck) {
        super(pAck);
    }
    
    @Override
    public final Object getResult() {
        return null;
    }
    
    @Override
    protected final void write(DataOutputStream pOut) {
        // nothing
    }
    
    @Override
    protected final void read(DataInputStream pIn) {
        // nothing
    }

}
