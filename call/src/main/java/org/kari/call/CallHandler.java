package org.kari.call;

import gnu.trove.map.TObjectShortMap;
import gnu.trove.map.TShortObjectMap;
import gnu.trove.map.hash.TObjectShortHashMap;
import gnu.trove.procedure.TShortObjectProcedure;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.IdentityHashMap;

import org.kari.call.event.BufferCall;
import org.kari.call.event.Call;

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

    private final short mServiceUUID;

    /**
     * <p>NOTE KI methodIds can be cached only within this handler; identitymap
     * won't hold across different proxy instances
     */
    private final IdentityHashMap<Method, Short> mMethodIds =
            new IdentityHashMap<Method, Short>();

    private final TObjectShortMap<Method> mMethods = new TObjectShortHashMap<Method>();


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
            return Integer.valueOf(0);
        }
        if ("equals".equals(methodName)) {
            return Boolean.FALSE;
        }

        int retryCount = 0;
        while (retryCount < RETRY_COUNT) {
            retryCount++;
            Object sessionId = mSessionProvider.getSessionId();
            ClientHandler handler = mClient.reserve(sessionId);
            try {
                boolean sessionIdChanged = sessionId != handler.getLastSessionId();
                handler.setLastSessionId(sessionId);

                Call call = new BufferCall(
                        sessionId,
                        sessionIdChanged,
                        mServiceUUID,
                        getMethodId(mServiceUUID, pMethod),
                        pArgs);

                result = handler.invoke(call);
                retryCount = RETRY_COUNT;
            } catch (RetryCallException e) {
                // retry
                if (retryCount >= RETRY_COUNT) {
                    Throwable cause = e.getCause();
                    if ( !(cause instanceof RemoteException) ) {
                        cause = new RemoteException("Connection failed", cause);
                    }
                    throw cause;
                }

                // small delay before retry
                Thread.sleep(50);
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
    private synchronized short getMethodId(int pServiceUUID, Method pTarget) {
        Short id = mMethodIds.get(pTarget);
        if (id == null) {
            if (mMethods.isEmpty()) {
                TShortObjectMap<Method> methods = mClient.getRegistry().getResolver().resolveMethods(mService);
                if (methods == null) {
                    methods = DefaultIdResolver.INSTANCE.resolveMethods(mService);
                }

                methods.forEachEntry(new TShortObjectProcedure<Method>() {
                    @Override
                    public boolean execute(short pMethodId, Method pMethod) {
                        mMethods.put(pMethod, pMethodId);
                        return true;
                    }
                });
            }

            mMethodIds.put(pTarget,  new Short(mMethods.get(pTarget)));
            id = mMethodIds.get(pTarget);
        }

        return id != null ? id.shortValue() : 0;
    }

}
