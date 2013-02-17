package org.kari.io.call;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.Remote;

/**
 * Remote call
 *
 * @author kari
 */
public final class StreamCall extends Call {
    
    private boolean mSessionIdChanged;
    private int mServiceUUID;
    private long mMethodId;
    private Object[] mParams;
    
    /**
     * For server side handling
     */
    public StreamCall() {
        super(CallType.STREAM_CALL, null);
    }
    
    /**
     * @param pSessionId Identifies session for session authentication, 
     * can be null
     * @param pParams null if no params
     */
    public StreamCall(
            Object pSessionId,
            boolean pSessionIdChanged,
            int pServiceUUID, 
            long pMethodId,
            Object[] pParams) 
    {
        super(CallType.STREAM_CALL, pSessionId);
    
        mSessionIdChanged = pSessionIdChanged;
        mServiceUUID = pServiceUUID;
        mMethodId = pMethodId;
        mParams = pParams;
    }
    
    @Override
    protected void write(DataOutputStream pOut) 
        throws Exception
    {
        pOut.writeInt(mServiceUUID);
        pOut.writeLong(mMethodId);
        pOut.write(mParams != null ? mParams.length : 0);
    
        ObjectOutputStream oo = createObjectOut(pOut);

        oo.writeBoolean(mSessionIdChanged);
        if (mSessionIdChanged) {
            oo.writeObject(mSessionId);
        }
        
        // parameters
        if (mParams != null) {
            for (int i = 0; i < mParams.length; i++) {
                oo.writeObject(mParams[i]);
            }
        }
        
        oo.flush();
    }

    @Override
    protected void read(DataInputStream pIn) 
        throws IOException,
            ClassNotFoundException
    {
        mServiceUUID = pIn.readInt();
        mMethodId = pIn.readLong();
        int count = pIn.read();
    
        ObjectInputStream oi = createObjectInput(pIn);

        mSessionIdChanged = oi.readBoolean();
        if (mSessionIdChanged) {
            mSessionId = oi.readObject();
        }
        
        if (count > 0) {
            mParams = new Object[count];
            for (int i = 0; i < count; i++) {
                mParams[i] = oi.readObject();
            }
        }
    }

    @Override
    public Result invoke(ServiceRegistry pRegistry) 
        throws Throwable
    {
        final Remote service = pRegistry.getService(mServiceUUID);
        final Method method = pRegistry.getMethod(mServiceUUID, mMethodId);
        
        try {
            Object result = method.invoke(service, mParams);
            return result != null 
                ? new StreamResult(result) 
                : NullResult.INSTANCE;
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }
    }
}
