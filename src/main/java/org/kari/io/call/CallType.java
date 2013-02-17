package org.kari.io.call;

import java.rmi.RemoteException;


public enum CallType {
    UNKNOWN(0, null),
    STREAM_CALL(1, StreamCall.class),
    STREAM_RESULT(10, StreamResult.class),
    RESULT_NULL(11, NullResult.class),
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
