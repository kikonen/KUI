package org.kari.io.call;

import org.apache.log4j.Logger;

/**
 * Base class of calls
 *
 * @author kari
 */
public abstract class Call extends Base {
    public static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".call");
    
    public Call(CallType pType) {
        super(pType);
    }
    
    /**
     * Invoke service
     * 
     * @return result, NullResult.INSTANCE for null
     */
    public abstract Result invoke(ServiceRegistry pRegistry)
        throws Throwable;
    
}
