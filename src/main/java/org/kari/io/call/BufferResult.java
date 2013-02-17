package org.kari.io.call;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.kari.io.DirectByteArrayOutputStream;

/**
 * Result based into intermediate buffer use
 *
 * @author kari
 */
public final class BufferResult extends Result {
    private Object mResult;
 
    /**
     * For decoding result
     */
    public BufferResult() {
        super(CallType.BUFFER_RESULT);
    }

    public BufferResult(Object pResult) {
        super(CallType.BUFFER_RESULT);
        mResult = pResult;
    }


    @Override
    public Object getResult() {
        return mResult;
    }

    @Override
    protected void write(Handler pHandler, DataOutputStream pOut)
        throws Exception 
    {
        DirectByteArrayOutputStream buffer = pHandler.getBuffer();
        
        ObjectOutputStream oo = createObjectOut(buffer, false);
        oo.writeObject(mResult);
        oo.flush();

        BufferCall.writeBuffer(buffer, pOut);
    }

    @Override
    protected void read(Handler pHandler, DataInputStream pIn)
        throws 
            IOException, 
            ClassNotFoundException 
    {
        DirectByteArrayOutputStream buffer = BufferCall.readBuffer(pHandler, pIn);
        
        ObjectInputStream oi = createObjectInput(
                new ByteArrayInputStream(
                        buffer.getBuffer(),  
                        0,  
                        buffer.size()),
                false);
        
        mResult = oi.readObject();
        
    }


}
