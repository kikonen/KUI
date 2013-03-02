package org.kari.call.event;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.kari.call.CallType;
import org.kari.call.Handler;

/**
 * Result based into intermediate buffer use
 *
 * @author kari
 */
public class BufferResult extends Result {
    protected Object mResult;
 
    /**
     * For decoding result
     */
    public BufferResult() {
        super();
    }

    public BufferResult(Object pResult) {
        super();
        mResult = pResult;
    }

    @Override
    public CallType getType() {
        return CallType.BUFFER_RESULT;
    }

    @Override
    public Object getResult() 
        throws Throwable
    {
        return mResult;
    }

    @Override
    protected void write(Handler pHandler, DataOutputStream pOut)
        throws Exception 
    {
        ObjectOutputStream oo = pHandler.createObjectOut();
        oo.writeObject(mResult);
        oo.flush();
        
        BufferCall.writeBuffer(pHandler, pOut);
    }

    @Override
    protected void read(Handler pHandler, DataInputStream pIn)
        throws 
            IOException, 
            ClassNotFoundException 
    {
        ObjectInputStream oi = BufferCall.readBuffer(pHandler, pIn);
        mResult = oi.readObject();
    }

}
