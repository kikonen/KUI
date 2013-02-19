package org.kari.call;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.hash.THashSet;

import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

/**
 * Registry for services. Registry is bound to both call client and server
 *
 * @see CallClient
 * @see CallServer
 * 
 * @author kari
 */
public final class ServiceRegistry {
    /**
     * Actual service instances; only in server side
     */
    private final TIntObjectMap<Remote> mServices = new TIntObjectHashMap<Remote>();
    
    private final Set<Class<? extends Remote>> mRegistered = new THashSet<Class<? extends Remote>>();
    
    private final TIntObjectMap<String> mNames = new TIntObjectHashMap<String>();
    
    private final TIntObjectMap<TLongObjectMap<Method>> mMethods = 
            new TIntObjectHashMap<TLongObjectMap<Method>>();
    
    public ServiceRegistry() {
        super();
    }
    
    /**
     * Find service from registry
     * 
     * @return null if not found
     */
    public synchronized String getServiceName(int pServiceUUID) {
        return mNames.get(pServiceUUID);
    }
    
    /**
     * @return null if not found
     */
    public synchronized Remote getService(int pServiceUUID) {
        return mServices.get(pServiceUUID);
    }
    
    /**
     * @return null if not found
     */
    public synchronized Method getMethod(int pServiceUUID, long pMethodId) {
        TLongObjectMap<Method> methods = mMethods.get(pServiceUUID);
        return methods != null ? methods.get(pMethodId) : null;
    }
    
    public synchronized int getServiceUUID(Class<? extends Remote> pService) {
        return CallUtil.getUUID(pService);
    }
    
    /**
     * Register service instance in server side
     * 
     * @throws InvalidServiceException if sevice is somehow invalid
     */
    public synchronized void register(Remote pService) 
        throws InvalidServiceException
    {
        Class<? extends Remote> cls = CallUtil.getRemote(pService.getClass());
        int uuid = register(cls);
        if (uuid != 0) {
            mServices.put(uuid, pService);
        }
    }
    
    /**
     * Register service API in client side
     * 
     * @return uuid of service, 0 if already registered
     * 
     * @throws InvalidServiceException if service is somehow invalid
     */
    public synchronized int register(Class<? extends Remote> pService) 
        throws InvalidServiceException
    {
        int uuid = 0;
        if (!mRegistered.contains(pService)) {
            mRegistered.add(pService);
            uuid = CallUtil.getUUID(pService);
            
            mNames.put(uuid, CallUtil.getName(pService));
            
            mMethods.put(uuid, collectMethods(pService));
        }        
        
        return uuid;
    }

    /**
     * Collect valid "remote" methods. Remote method is required to throw
     * RemoteException to allow exceptions from framework to be thrown.
     */
    private TLongObjectMap<Method> collectMethods(
            Class<? extends Remote> pService) 
        throws InvalidServiceException 
    {
        TLongObjectMap<Method> methods = new TLongObjectHashMap<Method>();
        
        for (Method method : pService.getMethods()) {
            boolean valid = false;
            
            for (Class exType : method.getExceptionTypes()) {
                valid |= RemoteException.class.isAssignableFrom(exType);
            }

            if (valid) {
                long methodId = CallUtil.getMethodId(method);
                if (methods.containsKey(methodId)) {
                    throw new InvalidServiceException("duplicate methoidId: " + method);
                }
                methods.put(methodId, method);
            }
        }
        
        return methods;
    }

}
