package org.kari.call.event;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.kari.call.CallType;
import org.kari.call.Handler;

/**
 * Result for {@link StreamCall}
 * 
 * @author kari
 */
public final class StreamResult extends Result {
    private Object mResult;
    
    /**
     * For result reading
     */
    public StreamResult() {
        super();
    }
    
    public StreamResult(Object pResult) {
        super();
        mResult = pResult;
    }
    
    @Override
    public CallType getType() {
        return CallType.STREAM_RESULT;
    }

    @Override
    public Object getResult() {
        return mResult;
    }
    
    @Override
    protected void write(Handler pHandler, DataOutputStream pOut) 
        throws Exception 
    {
        ObjectOutputStream oo = pHandler.getIOFactory().createObjectOutput(pOut);
        
        oo.writeObject(mResult);
        
        oo.flush();
    }
    
    @Override
    protected void read(Handler pHandler, DataInputStream pIn) 
        throws IOException,
            ClassNotFoundException 
    {
        ObjectInputStream oi = pHandler.getIOFactory().createObjectInput(pIn);
        
        mResult = oi.readObject();
    }
    
}
