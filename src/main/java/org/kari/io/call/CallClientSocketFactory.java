package org.kari.io.call;

import java.io.IOException;
import java.net.Socket;

/**
 * API for creating sockets for client side
 *
 * @author kari
 */
public interface CallClientSocketFactory {
    /**
     * @param pServerAddress Server identifier (DNS, IP, URL, etc.)
     */
    Socket createSocket(
            String pServerAddress, 
            int pPort)
        throws 
            IOException;
}
