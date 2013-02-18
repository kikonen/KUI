package org.kari.call.event;

import java.io.DataInputStream;
import java.io.DataOutputStream;

import org.kari.call.CallType;
import org.kari.call.Handler;


/**
 * Null result
 *
 * @author kari
 */
public final class NullResult extends Result {
    public static final NullResult INSTANCE = new NullResult();
    
    public NullResult() {
        super(CallType.RESULT_NULL);
    }
    
    @Override
    public Object getResult() {
        return null;
    }
    
    @Override
    protected void write(Handler pHandler, DataOutputStream pOut) {
        // nothing
    }
    
    @Override
    protected void read(Handler pHandler, DataInputStream pIn) {
        // nothing
    }

}
