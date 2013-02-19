package org.kari.call;

import java.lang.reflect.Method;
import java.rmi.Remote;

/**
 * Invoke method in 
 *
 * @author kari
 */
public interface CallInvoker {
    /**
     * Invoke pService.pMethod with pParam
     * 
     * @param pSessionId Session id from client
     * @return return value from pMethod
     * 
     * <p>NOTE KI caller will check for InvocationTargetException so implementation
     * doesn't need to handle it.
     */
    Object invoke(
            Object pSessionId,
            Remote pService,
            Method pMethod,
            Object[] pParams) 
        throws Throwable;

}
