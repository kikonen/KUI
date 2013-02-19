package org.kari.call.event;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.Remote;

import org.kari.call.CallInvoker;
import org.kari.call.CallType;
import org.kari.call.RemoteMethodNotFoundException;
import org.kari.call.ServiceRegistry;

/**
 * Calls based into service/method principle
 *
 * @author kari
 */
public abstract class ServiceCall extends Call {
    protected int mServiceUUID;
    protected long mMethodId;
    protected Object[] mParams;
    

    /**
     * For server side handling
     */
    protected ServiceCall(CallType pType) {
        super(pType, null, false);
    }
    
    /**
     * @param pSessionId Identifies session for session authentication, 
     * can be null
     * @param pParams null if no params
     */
    protected ServiceCall(
            CallType pType,
            Object pSessionId,
            boolean pSessionIdChanged,
            int pServiceUUID, 
            long pMethodId,
            Object[] pParams) 
    {
        super(pType, pSessionId, pSessionIdChanged);
    
        mServiceUUID = pServiceUUID;
        mMethodId = pMethodId;
        mParams = pParams;
    }

    @Override
    public final Result invoke(
            ServiceRegistry pRegistry,
            CallInvoker pInvoker) 
        throws Throwable
    {
        final Remote service = pRegistry.getService(mServiceUUID);
        final Method method = pRegistry.getMethod(mServiceUUID, mMethodId);

        if (service== null) {
            throw new RemoteMethodNotFoundException("Service not found");
        }

        if (method == null) {
            throw new RemoteMethodNotFoundException("Method not found");
        }
        
        try {
            Object result = pInvoker.invoke(mSessionId, service, method, mParams);
            return result != null 
                ? createResult(result) 
                : NullResult.INSTANCE;
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }

    protected abstract Result createResult(Object pResult);

    /**
     * Standard write logic for service call
     */
    protected final void write(ObjectOutputStream pOut) 
        throws Exception
    {
        pOut.writeInt(mServiceUUID);
        pOut.writeLong(mMethodId);
        pOut.write(mParams != null ? mParams.length : 0);
    
        pOut.writeBoolean(mSessionIdChanged);
        if (mSessionIdChanged) {
            pOut.writeObject(mSessionId);
        }
        
        // parameters
        if (mParams != null) {
            for (int i = 0; i < mParams.length; i++) {
                pOut.writeObject(mParams[i]);
            }
        }
    }

    /**
     * Standard read logic for service call
     */
    protected final void read(ObjectInputStream pIn) 
        throws IOException,
            ClassNotFoundException
    {
        mServiceUUID = pIn.readInt();
        mMethodId = pIn.readLong();
        int count = pIn.read();

        mSessionIdChanged = pIn.readBoolean();
        if (mSessionIdChanged) {
            mSessionId = pIn.readObject();
        }
        
        if (count > 0) {
            mParams = new Object[count];
            for (int i = 0; i < count; i++) {
                mParams[i] = pIn.readObject();
            }
        }
    }

}
