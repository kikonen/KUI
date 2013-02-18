package org.kari.call.event;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.kari.call.CallType;
import org.kari.call.Handler;

/**
 * Different acks
 *
 * @author kari
 */
public abstract class AckResult extends Result {
    protected AckResult(CallType pAck) {
        super(pAck);
    }
    
    @Override
    public final Object getResult() {
        return null;
    }
    
    @Override
    protected final void write(Handler pHandler, DataOutputStream pOut) {
        // nothing
    }
    
    @Override
    protected final void read(Handler pHandler, DataInputStream pIn) {
        // nothing
    }

}
