package org.kari.call;

import java.lang.reflect.Method;
import java.rmi.Remote;

public interface IdResolver {
    /**
     * UUID for pMethod. Id needs to be unique only within context of
     * single service
     */
    long getMethodId(Method pMethod);

    /**
     * Name of service
     */
    String getName(Class<? extends Remote> pService);

    /**
     * UUID value for service
     */
    int getUUID(Class<? extends Remote> pService);

}
