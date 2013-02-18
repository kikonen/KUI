package org.kari.call;

import java.rmi.RemoteException;

/**
 * If service is somehow invalid
 *
 * @author kari
 */
public final class InvalidServiceException extends RemoteException {

    public InvalidServiceException() {
        super();
    }
    
    public InvalidServiceException(String pMessage, Throwable pCause) {
        super(pMessage, pCause);
    }
    
    public InvalidServiceException(String pMessage) {
        super(pMessage);
    }

}
