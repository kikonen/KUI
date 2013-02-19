package org.kari.call;

import java.lang.reflect.Method;
import java.rmi.Remote;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Default resolver logic
 *
 * @author kari
 */
public final class DefaultIdResolver implements IdResolver {
    public static final DefaultIdResolver INSTANCE = new DefaultIdResolver();
    
    private final MessageDigest MD5;
    

    public DefaultIdResolver() {
        try {
            MD5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            // occurs only if internal error;
            throw new RuntimeException(e);
        }
    }

    @Override
    public synchronized long getMethodId(Method pMethod) 
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
    
    @Override
    public String getName(Class<? extends Remote> pService) {
        ServiceName tag = pService.getAnnotation(ServiceName.class);
        return tag.name();
    }
    
    @Override
    public int getUUID(Class<? extends Remote> pService) {
        ServiceName tag = pService.getAnnotation(ServiceName.class);
        return tag.id();
    }
    

}
