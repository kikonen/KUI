package org.kari.call.event;

import org.apache.log4j.Logger;
import org.kari.call.CallConstants;
import org.kari.call.CallInvoker;
import org.kari.call.ServiceRegistry;

/**
 * Base class of calls
 *
 * @author kari
 */
public abstract class Call extends CallEvent {
    public static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".call");

    protected Object mSessionId;
    protected boolean mSessionIdChanged;


    protected Call() {
        super();
    }

    protected Call(Object pSessionId, boolean pSessionIdChanged) {
        super();

        mSessionId = pSessionId;
        mSessionIdChanged = pSessionIdChanged;
    }

    public final Object getSessionId() {
        return mSessionId;
    }

    public void setSessionId(Object pSessionId) {
        mSessionId = pSessionId;
    }

    public boolean isSessionIdChanged() {
        return mSessionIdChanged;
    }

    /**
     * Invoke service
     *
     * <p>NOTE KI invoke() is responsible for unwrapping possible
     * InvocationTargetException or such.
     *
     * @return result, NullResult.INSTANCE for null
     */
    public abstract Result invoke(
            ServiceRegistry pRegistry,
            CallInvoker pInvoker)
        throws Throwable;

}
