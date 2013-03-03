package org.kari.call;

import gnu.trove.map.TShortObjectMap;
import gnu.trove.map.hash.TShortObjectHashMap;
import gnu.trove.set.hash.THashSet;

import java.lang.reflect.Method;
import java.rmi.Remote;
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
    private final TShortObjectMap<Remote> mServices = new TShortObjectHashMap<Remote>();
    
    private final Set<Class<? extends Remote>> mRegistered = new THashSet<Class<? extends Remote>>();
    
    private final TShortObjectMap<String> mNames = new TShortObjectHashMap<String>();
    
    private final TShortObjectMap<TShortObjectMap<Method>> mMethods = 
            new TShortObjectHashMap<TShortObjectMap<Method>>();
    
    private final IdResolver mResolver;

    
    /**
     * @param pResolver null for default resolver
     */
    public ServiceRegistry(IdResolver pResolver) {
        mResolver = pResolver != null 
            ? pResolver 
            : DefaultIdResolver.INSTANCE;
    }

    public IdResolver getResolver() {
        return mResolver;
    }

    /**
     * Find service from registry
     * 
     * @return null if not found
     */
    public synchronized String getServiceName(short pServiceUUID) {
        return mNames.get(pServiceUUID);
    }
    
    /**
     * @return null if not found
     */
    public synchronized Remote getService(short pServiceUUID) {
        return mServices.get(pServiceUUID);
    }
    
    /**
     * @return null if not found
     */
    public synchronized Method getMethod(short pServiceUUID, short pMethodId) {
        TShortObjectMap<Method> methods = mMethods.get(pServiceUUID);
        return methods != null ? methods.get(pMethodId) : null;
    }
    
    public short getServiceUUID(Class<? extends Remote> pService) {
        return mResolver.getUUID(pService);
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
        short uuid = register(cls);
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
    public synchronized short register(Class<? extends Remote> pService) 
        throws InvalidServiceException
    {
        short uuid = 0;
        if (!mRegistered.contains(pService)) {
            mRegistered.add(pService);
            uuid = mResolver.getUUID(pService);
            
            mNames.put(uuid, mResolver.getName(pService));
            
            mMethods.put(uuid, collectMethods(pService));
        }        
        
        return uuid;
    }

    /**
     * Collect valid "remote" methods. Remote method is required to throw
     * RemoteException to allow exceptions from framework to be thrown.
     */
    private TShortObjectMap<Method> collectMethods(
            Class<? extends Remote> pService) 
        throws InvalidServiceException 
    {
        TShortObjectMap<Method> result = mResolver.resolveMethods(pService);
        
        if (result == null) {
            result = DefaultIdResolver.INSTANCE.resolveMethods(pService);
        }
        
        return result;
    }

}
