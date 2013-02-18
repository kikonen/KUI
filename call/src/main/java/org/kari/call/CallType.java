package org.kari.call;

import java.rmi.RemoteException;

import org.kari.call.event.AckCallReceived;
import org.kari.call.event.Base;
import org.kari.call.event.BufferCall;
import org.kari.call.event.BufferResult;
import org.kari.call.event.ErrorResult;
import org.kari.call.event.NullResult;
import org.kari.call.event.StreamCall;
import org.kari.call.event.StreamResult;


public enum CallType {
    UNKNOWN(0, null),
    STREAM_CALL(1, StreamCall.class),
    STREAM_RESULT(10, StreamResult.class),
    
    BUFFER_CALL(20, BufferCall.class),
    BUFFER_RESULT(21, BufferResult.class),

    RESULT_NULL(30, NullResult.class),
    ERROR(50, ErrorResult.class),
    ACK_CALL_RECEIVED(100, AckCallReceived.class);
    
    public final byte mCode;
    public final Class<? extends Base> mClass;
    
    private CallType(int pCode, Class<? extends Base> pClass) {
        mCode = (byte)pCode;
        mClass = pClass;
    }
    
    /**
     * @return type, UNKNOWN if not found
     */
    public static CallType resolve(int pCode) {
        for (CallType type : values()) {
            if (type.mCode == pCode) {
                return type;
            }
        }
        return UNKNOWN;
    }
    
    public Base create() throws RemoteException {
        if (this == RESULT_NULL) {
            return NullResult.INSTANCE;
        }else if (this == ACK_CALL_RECEIVED) {
            return AckCallReceived.INSTANCE;
        }
        
        try {
            return mClass.newInstance();
        } catch (Exception e) {
            throw new RemoteException("Unsupported type", e);
        }
    }
}
