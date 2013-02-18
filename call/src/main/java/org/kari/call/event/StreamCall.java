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
     * Compression is preferred in out/in
     */
    public static final boolean COMPRESS = true;
    
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
        ObjectOutputStream oo = pHandler.getIOFactory().createObjectOutput(pOut, COMPRESS);
        write(oo);
        oo.flush();
    }

    @Override
    protected void read(Handler pHandler, DataInputStream pIn) 
        throws IOException,
            ClassNotFoundException
    {
        ObjectInputStream oi = pHandler.getIOFactory().createObjectInput(pIn, COMPRESS);
        read(oi);
    }
    
    @Override
    protected Result createResult(Object pResult) {
        return new StreamResult(pResult);
    }
}
