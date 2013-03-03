package org.kari.call;

import gnu.trove.map.TShortObjectMap;
import gnu.trove.map.hash.TShortObjectHashMap;

import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
    public TShortObjectMap<Method> resolveMethods(Class<? extends Remote> pService) 
    {
        TShortObjectMap<Method> result = new TShortObjectHashMap<Method>();

        List<Method> methods = new ArrayList<Method>();
        for (Method method : pService.getMethods()) {
            boolean valid = false;
            
            for (Class exType : method.getExceptionTypes()) {
                valid |= RemoteException.class.isAssignableFrom(exType)
                    || Exception.class == exType;
            }

            if (valid) {
                methods.add(method);
            }
        }
        
        Collections.sort(methods, MethodComparator.INSTANCE);
        
        for (short i = 0; i < methods.size(); i++) {
            result.put((short)(i + 1),  methods.get(i));
        }
        
        return result;
    }


    @Override
    public String getName(Class<? extends Remote> pService) {
        ServiceName tag = pService.getAnnotation(ServiceName.class);
        return tag.name();
    }
    
    @Override
    public short getUUID(Class<? extends Remote> pService) {
        ServiceName tag = pService.getAnnotation(ServiceName.class);
        return tag.id();
    }
    

}
