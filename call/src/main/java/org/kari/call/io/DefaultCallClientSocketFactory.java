package org.kari.call.io;

import java.io.IOException;
import java.net.Socket;

/**
 * Default plain socket factory
 *
 * @author kari
 */
public final class DefaultCallClientSocketFactory 
    implements
        CallClientSocketFactory
{
    public static final CallClientSocketFactory INSTANCE = new DefaultCallClientSocketFactory();

    @Override
    public Socket createSocket(String pServerAddress, int pPort)
        throws IOException 
    {
        return new Socket(pServerAddress, pPort);
   }
    
}
