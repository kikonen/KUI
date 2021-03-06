package org.kari.call.event;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.kari.call.CallType;
import org.kari.call.Handler;

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
        super();
    }

    /**
     * @param pSessionId Identifies session for session authentication, 
     * can be null
     * @param pParams null if no params
     */
    public StreamCall(
            Object pSessionId,
            boolean pSessionIdChanged,
            short pServiceUUID, 
            short pMethodId,
            Object[] pParams) 
    {
        super(pSessionId, pSessionIdChanged, pServiceUUID, pMethodId, pParams);
    }
    
    @Override
    public CallType getType() {
        return CallType.STREAM_CALL;
    }
    
    @Override
    protected void write(Handler pHandler, DataOutputStream pOut) 
        throws Exception
    {
        ObjectOutputStream oo = pHandler.getIOFactory().createObjectOutput(pOut);
        writeObjectOut(oo);
        oo.flush();
    }

    @Override
    protected void read(Handler pHandler, DataInputStream pIn) 
        throws IOException,
            ClassNotFoundException
    {
        ObjectInputStream oi = pHandler.getIOFactory().createObjectInput(pIn);
        readObjectIn(oi);
    }
    
    @Override
    protected Result createResult(Object pResult) {
        return new StreamResult(pResult);
    }
}
