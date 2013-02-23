package org.kari.call.io;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

/**
 * Default plain socket factory
 *
 * @author kari
 */
public final class DefaultCallServerSocketFactory 
    implements
        CallServerSocketFactory
{
    public static final CallServerSocketFactory INSTANCE = new DefaultCallServerSocketFactory();

    @Override
    public ServerSocket createSocket(String pServerAddress, int pPort)
            throws IOException 
    {
        InetAddress address = pServerAddress != null
            ? InetAddress.getByName(pServerAddress)
            : null;
        return new ServerSocket(pPort, 50, address);
    }
    
    

}
