package org.kari.call;

import java.lang.reflect.Method;
import java.rmi.Remote;

/**
 * default invoker used by framework
 *
 * @author kari
 */
public final class DefaultCallInvoker implements CallInvoker {
    public static final DefaultCallInvoker INSTANCE = new DefaultCallInvoker();
    
    @Override
    public Object invoke(
            Object pSessionId, 
            Remote pService, 
            Method pMethod,
            Object[] pParams)
    throws Throwable 
    {
        return pMethod.invoke(pService, pParams);
    }

}
