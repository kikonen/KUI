package org.kari.call;

import java.rmi.RemoteException;

/**
 * Either service or method not found
 *
 * @author kari
 */
public final class RemoteMethodNotFoundException extends RemoteException {

    public RemoteMethodNotFoundException(String pMessage) {
        super(pMessage);
    }

}
