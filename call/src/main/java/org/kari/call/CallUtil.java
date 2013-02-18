package org.kari.call;

import java.io.Closeable;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.rmi.Remote;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.log4j.Logger;

/**
 * Shared utilities for remote calls
 *
 * @author kari
 */
public class CallUtil {
    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".util");

    private static final MessageDigest MD5;
    
    static {
        try {
            MD5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // occurs only if internal error;
            throw new RuntimeException(e);
        }
    }

    public static String getName(Class<? extends Remote> pService) {
        ServiceName tag = pService.getAnnotation(ServiceName.class);
        return tag.name();
    }
    
    public static int getUUID(Class<? extends Remote> pService) {
        ServiceName tag = pService.getAnnotation(ServiceName.class);
        return tag.id();
    }
    
    public static synchronized long getMethodId(Method pMethod) 
    {
        String desc = pMethod.toString();
        
        MD5.reset();
        byte[] digest = MD5.digest(desc.getBytes());
        
        long value = 0;
        for (int i = 0; i < digest.length; i += 2) {
           value = (value << 8) + ((digest[i] ^ digest[i + 1]) & 0xff);
        }
        
        return value;
    }
    
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
