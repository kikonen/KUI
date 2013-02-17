package org.kari.io.call;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Remote call
 *
 * @author kari
 */
public final class StreamCall extends ServiceCall {
    /**
     * For decoding call
     */
    public StreamCall() {
        super(CallType.STREAM_CALL);
    }
    
    /**
     * @param pSessionId Identifies session for session authentication, 
     * can be null
     * @param pParams null if no params
     */
    public StreamCall(
            Object pSessionId,
            boolean pSessionIdChanged,
            int pServiceUUID, 
            long pMethodId,
            Object[] pParams) 
    {
        super(CallType.STREAM_CALL, pSessionId, pSessionIdChanged, pServiceUUID, pMethodId, pParams);
    }
    
    @Override
    protected void write(Handler pHandler, DataOutputStream pOut) 
        throws Exception
    {
        ObjectOutputStream oo = createObjectOut(pOut);
        write(oo);
        oo.flush();
    }

    @Override
    protected void read(Handler pHandler, DataInputStream pIn) 
        throws IOException,
            ClassNotFoundException
    {
        ObjectInputStream oi = createObjectInput(pIn);
        read(oi);
    }
    
    @Override
    protected Result createResult(Object pResult) {
        return new StreamResult(pResult);
    }
}
