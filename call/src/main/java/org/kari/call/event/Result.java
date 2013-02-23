package org.kari.call.event;

import org.apache.log4j.Logger;
import org.kari.call.CallConstants;

/**
 * Base type of different results
 *
 * @author kari
 */
public abstract class Result extends Base {
    public static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".result");
    
    
    protected Result() {
        super();
    }
    
    /**
     * @return result, can be null
     * 
     * @throws Throwable In case result triggers exception instead of result
     * value
     */
    public abstract Object getResult()
        throws Throwable;
    
    
    /**
     * In case failed to send result back to client, allow result to trace
     * info for troubleshooting
     */
    public void traceDebug() {
        // nothing
    }
}
