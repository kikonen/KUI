package org.kari.call;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.IdentityHashMap;

import org.kari.call.event.Base;
import org.kari.call.event.BufferCall;
import org.kari.call.event.Call;
import org.kari.call.event.StreamCall;

/**
 * Proxy for remote calls
 *
 * @author kari
 */
public final class CallHandler implements InvocationHandler {
    private static final int RETRY_COUNT = 2;
    
    private final Class<? extends Remote> mService;
    private final CallClient mClient;
    private final CallSessionProvider mSessionProvider;
    
    private final int mServiceUUID;
    
    /**
     * <p>NOTE KI methodIds can be cached only within this handler; identitymap
     * won't hold across different proxy instances
     */
    private final IdentityHashMap<Method, Long> mMethodIds = 
            new IdentityHashMap<Method, Long>();
    
    private Object mLastSessionId;
    
    
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
        Object result = null;
        String methodName = pMethod.getName();
        
        if ("toString".equals(methodName)) {
            return mService.toString();
        }
        if ("hashCode".equals(methodName)) {
            return 0;
        }
        if ("equals".equals(methodName)) {
            return false;
        }

        int retryCount = 0;
        while (retryCount < RETRY_COUNT) {
            retryCount++;
            ClientHandler handler = mClient.reserve();
            try {
                Object sessionId = mSessionProvider.getSessionId();
                boolean sessionIdChanged = sessionId != mLastSessionId;
                mLastSessionId = sessionId;
                
                Call call;
                if (Base.BUFFER_CALL) {
                    call = new BufferCall(
                            sessionId,
                            sessionIdChanged,
                            mServiceUUID, 
                            getMethodId(mServiceUUID, pMethod), 
                            pArgs);
                } else {
                    call = new StreamCall(
                            sessionId,
                            sessionIdChanged,
                            mServiceUUID, 
                            getMethodId(mServiceUUID, pMethod), 
                            pArgs);
                }
                
                result = handler.invoke(call);
                retryCount = RETRY_COUNT;
            } catch (RetryCallException e) {
                // retry
                if (retryCount >= RETRY_COUNT) {
                    throw e;
                }
                
                // small delay before retry
                Thread.sleep(100);
            } catch (RemoteMethodNotFoundException e) {
                throw new RemoteException(e.getMessage() + ": " + pMethod);
            } finally {
                mClient.release(handler);
            }
        }
        
        return result;
    }
    
    
    /**
     * Get/Create methodId in client side
     */
    private synchronized long getMethodId(int pServiceUUID, Method pMethod) {
        Long id = mMethodIds.get(pMethod);
        if (id == null) {
            id = mClient.getRegistry().getResolver().getMethodId(pMethod);
            mMethodIds.put(pMethod,  new Long(id));
        }
        
        return id != null ? id.longValue() : 0;
    }

}