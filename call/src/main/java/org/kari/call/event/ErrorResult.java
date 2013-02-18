package org.kari.call.event;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.kari.call.CallType;
import org.kari.call.Handler;

/**
 * Error from server side
 *
 * @author kari
 */
public final class ErrorResult extends Result {
    private Throwable mError;
    
    /**
     * For error reading
     */
    public ErrorResult() {
        super(CallType.ERROR);
    }
    
    public ErrorResult(Throwable pError) {
        super(CallType.ERROR);
        mError = pError;
    }
    
    @Override
    public Object getResult() 
        throws Throwable
    {
        throw mError;
    }
    
    @Override
    protected void write(Handler pHandler, DataOutputStream pOut) 
        throws Exception 
    {
        ObjectOutputStream oo = pHandler.getIOFactory().createObjectOutput(pOut, true);
        
        oo.writeObject(mError);
        
        oo.flush();
    }
    
    @Override
    protected void read(Handler pHandler, DataInputStream pIn) 
        throws IOException,
            ClassNotFoundException 
    {
        ObjectInputStream oi = pHandler.getIOFactory().createObjectInput(pIn, true);
        
        mError = (Throwable)oi.readObject();
    }
    
    @Override
    public void traceDebug() {
        LOG.debug("Failed to send error back to client", mError);
    }    
}
