package org.kari.call;

import gnu.trove.map.TShortObjectMap;

import java.lang.reflect.Method;
import java.rmi.Remote;

public interface IdResolver {
    /**
     * Resolve ids for methods in service. Ids are unique within pService. 
     * Method ids must be positive values (i.e. "id > 0").
     * 
     * @return null to use builtin default logic
     */
    TShortObjectMap<Method> resolveMethods(Class<? extends Remote> pService);

    /**
     * Name of service
     */
    String getName(Class<? extends Remote> pService);

    /**
     * UUID value for service
     */
    short getUUID(Class<? extends Remote> pService);

}
