package org.kari.io.call;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * API for creating sockets for server side
 *
 * @author kari
 */
public interface CallServerSocketFactory {
    /**
     * @param pServerAddress Server identifier (DNS, IP, URL, etc.), can null
     */
    ServerSocket createSocket(
            String pServerAddress, 
            int pPort)
        throws 
            IOException;
}
