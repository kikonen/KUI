package org.kari.base;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.apache.log4j.Logger;
import org.kari.annotation.AssertType;
import org.kari.annotation.KAssert;
import org.kari.util.ClassUtil;

/**
 * Assert invocation handler
 * 
 * @author kari
 */
public final class AssertHandler implements InvocationHandler {

    public static final Logger LOG = Logger.getLogger("ki.assert");
    
    private final Object mObject;
    
    public AssertHandler(final Object pObject) {
        mObject = pObject;
    }

    @Override
    public Object invoke(
        final Object proxy,
        final Method pMethod,
        final Object[] pArgs) throws Throwable
    {
        Object result = pMethod.invoke(mObject, pArgs);
        KAssert ass = pMethod.getAnnotation(KAssert.class);
        if (ass!=null) {
            if (ass.value()==AssertType.NULL) {
                if (result==null) {
                    Exception ex = new NullPointerException(ass + " failed");
                    LOG.error(ass + " failed", ex);
                    throw ex;
                }
            }
        }
        return result;
    }

    /**
     * Create assert handler. All interfaces of Object are
     * asserted.
     */
    public static <T> T create(Object pObject) {
        Class[] types = ClassUtil.collectInterfaces(pObject.getClass());
        
        return (T)Proxy.newProxyInstance(
            AssertHandler.class.getClassLoader(), 
            types, 
            new AssertHandler(pObject));
    }
}
