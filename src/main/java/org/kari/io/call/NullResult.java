package org.kari.io.call;

import java.io.DataInputStream;
import java.io.DataOutputStream;


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
