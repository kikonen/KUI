package org.kari.call;

import java.io.Closeable;
import java.lang.reflect.Proxy;
import java.rmi.Remote;

import org.apache.log4j.Logger;

/**
 * Shared utilities for remote calls
 *
 * @author kari
 */
public class CallUtil {
    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".util");


    /**
     * Get remote interface for some pService class
     */
    public static Class<? extends Remote> getRemote(Class<? extends Remote> pService) {
        Class<? extends Remote> remote = null;
        Class<?> cls = pService;
        
        while (cls != null && remote == null) {
            if (cls.isInterface() && Remote.class.isAssignableFrom(cls)) {
                remote = (Class)cls;
            }
            
            if (remote == null) {
                for (Class interCls : cls.getInterfaces()) {
                    if (Remote.class.isAssignableFrom(interCls)) {
                        remote = interCls;
                        break;
                    }
                }
            }
    
            cls = cls.getSuperclass();
        }
        
        return remote;
    }
    
    
    /**
     * @return always null
     */
    public static <T> T closeSocket(Closeable pSocket) {
        if (pSocket != null) {
            try {
                pSocket.close();
            } catch (Exception e) {
                LOG.warn("Failed to close socket cleanly: " + pSocket, e);
            }
        }
        return null;
    }
    
    /**
     * Create proxy for remote calls
     * 
     * @param pService remote service interface
     */
    public static <T extends Remote> T makeProxy(
            Class<T> pService,
            CallClient pClient,
            CallSessionProvider pSessionProvider) 
        throws IllegalArgumentException,
            InvalidServiceException
    {
        Object service = Proxy.newProxyInstance(
                CallUtil.class.getClassLoader(), 
                new Class[]{pService}, 
                new CallHandler(pService, pClient, pSessionProvider));
        return (T)service;
    }

}
