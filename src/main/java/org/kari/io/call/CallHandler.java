package org.kari.io.call;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.util.IdentityHashMap;

/**
 * Proxy for remote calls
 *
 * @author kari
 */
public final class CallHandler implements InvocationHandler {
    private final Class<? extends Remote> mService;
    private final CallClient mClient;
    private final CallSessionProvider mSessionProvider;
    
    private final int mServiceUUID;
    
    /**
     * <p>NOTE KI methodIds can be cached only within this handler; identitymap
     * won't hold across different proxy instances
     */
    private final TIntObjectMap<IdentityHashMap<Method, Long>> mMethodIds = 
            new TIntObjectHashMap<IdentityHashMap<Method, Long>>();
    
    
    public CallHandler(
            Class<? extends Remote> pService,
            CallClient pClient, 
            CallSessionProvider pSessionProvider) 
        throws InvalidServiceException
    {
        mService = pService;
        mClient = pClient;
        mSessionProvider = pSessionProvider;
        
        mClient.getRegistry().register(pService);
        mServiceUUID = mClient.getRegistry().getServiceUUID(mService);
    }
    
    
    @Override
    public Object invoke(Object pProxy, Method pMethod, Object[] pArgs)
        throws Throwable 
    {
        ClientHandler handler = mClient.reserve();
        try {
            Call call = new StreamCall(
                    mSessionProvider.getSessionId(), 
                    mServiceUUID, 
                    getMethodId(mServiceUUID, pMethod), 
                    pArgs);
            
            return handler.invoke(call);
        } finally {
            mClient.release(handler);
        }
    }
    
    
    /**
     * Get/Create methodId in client side
     */
    private synchronized long getMethodId(int pServiceUUID, Method pMethod) {
        Long id = null;
        IdentityHashMap<Method, Long> methodIds = mMethodIds.get(pServiceUUID);
        if (methodIds ==null) {
            methodIds = new IdentityHashMap<Method, Long>();
            mMethodIds.put(pServiceUUID, methodIds);
        }
        id = methodIds.get(pMethod);
        if (id == null) {
            id = CallUtil.getMethodId(pMethod);
            methodIds.put(pMethod,  new Long(id));
        }
        
        return id != null ? id.longValue() : 0;
    }

}
