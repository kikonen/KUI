package org.kari.call;

import java.rmi.RemoteException;

/**
 * Call failed to sent to server, possible to retry call
 *
 * @author kari
 */
public final class RetryCallException extends RemoteException {

    public RetryCallException() {
        super();
    }

    public RetryCallException(String pS) {
        super(pS);
    }

    public RetryCallException(String pS, Throwable pCause) {
        super(pS, pCause);
    }

}
