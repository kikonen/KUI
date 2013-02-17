package org.kari.io.call;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Result for {@link StreamCall}
 * 
 * @author kari
 */
public final class StreamResult extends Result {
    public static final boolean COMPRESS = StreamCall.COMPRESS;
    
    
    private Object mResult;
    
    /**
     * For result reading
     */
    public StreamResult() {
        super(CallType.STREAM_RESULT);
    }
    
    public StreamResult(Object pResult) {
        super(CallType.STREAM_RESULT);
        mResult = pResult;
    }
    
    @Override
    public Object getResult() {
        return mResult;
    }
    
    @Override
    protected void write(DataOutputStream pOut) 
        throws Exception 
    {
        ObjectOutputStream oo = createObjectOut(pOut);
        
        oo.writeObject(mResult);
        
        oo.flush();
    }
    
    @Override
    protected void read(DataInputStream pIn) 
        throws IOException,
            ClassNotFoundException 
    {
        ObjectInputStream oi = createObjectInput(pIn);
        
        mResult = oi.readObject();
    }
    
}
