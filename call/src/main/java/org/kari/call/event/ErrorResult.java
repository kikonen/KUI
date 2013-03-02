package org.kari.call.event;

import org.kari.call.CallType;

/**
 * Error from server side
 *
 * @author kari
 */
public final class ErrorResult extends BufferResult {
    /**
     * For error reading
     */
    public ErrorResult() {
        super();
    }
    
    public ErrorResult(Throwable pError) {
        super(pError);
    }
    
    @Override
    public CallType getType() {
        return CallType.ERROR;
    }

    @Override
    public Object getResult() 
        throws Throwable
    {
        throw (Throwable)mResult;
    }
    
    @Override
    public void traceDebug() {
        LOG.debug("Failed to send error back to client", (Throwable)mResult);
    }    
}
