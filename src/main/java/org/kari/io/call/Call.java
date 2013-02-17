package org.kari.io.call;

import org.apache.log4j.Logger;

/**
 * Base class of calls
 *
 * @author kari
 */
public abstract class Call extends Base {
    public static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".call");

    protected Object mSessionId;
    protected boolean mSessionIdChanged;

    
    protected Call(CallType pType, Object pSessionId, boolean pSessionIdChanged) {
        super(pType);
        
        mSessionId = pSessionId;
        mSessionIdChanged = pSessionIdChanged;
    }
    
    public final Object getSessionId() {
        return mSessionId;
    }
    
    public void setSessionId(Object pSessionId) {
        mSessionId = pSessionId;
    }

    /**
     * Invoke service
     * 
     * @return result, NullResult.INSTANCE for null
     */
    public abstract Result invoke(ServiceRegistry pRegistry)
        throws Throwable;
    
}
