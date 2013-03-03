package org.kari.call;

import java.io.Closeable;
import java.io.DataOutput;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Proxy;
import java.rmi.Remote;

import org.apache.log4j.Logger;

/**
 * Shared utilities for remote calls
 *
 * @author kari
 */
public class CallUtil {
    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".util");

    /**
     * Write positive pValue using minimal amount of bytes, 1, 2 or 4 bytes
     */
    public static void writeCompactInt(OutputStream pOut, int pValue) 
        throws IOException
    {
        if (pValue < 0x40) {
            // 1 byte
            pOut.write( pValue );
        } else if (pValue < 0x3FFF) {
            // 2 byte
            int ch1 = (pValue >>> 8) & 0xFF; 
            int ch2 = (pValue >>> 0) & 0xFF;
            ch1 |= 0x80;
            
            pOut.write(ch1);
            pOut.write(ch2);
        } else if (pValue < 0x3FFFFFFF) {
            // 4 byte
            int ch1 = (pValue >>> 24) & 0xFF; 
            int ch2 = (pValue >>> 16) & 0xFF;
            int ch3 = (pValue >>> 8) & 0xFF;
            int ch4 = (pValue >>> 0) & 0xFF;
            ch1 |= 0x40;
            
            pOut.write(ch1);
            pOut.write(ch2);
            pOut.write(ch3);
            pOut.write(ch4);
        } else {
            // 5 bytes
            int ch1 = (pValue >>> 24) & 0xFF; 
            int ch2 = (pValue >>> 16) & 0xFF;
            int ch3 = (pValue >>> 8) & 0xFF;
            int ch4 = (pValue >>> 0) & 0xFF;
            
            pOut.write(0xFF);
            pOut.write(ch1);
            pOut.write(ch2);
            pOut.write(ch3);
            pOut.write(ch4);
        }
    }

    /**
     * Read positive int encoded via {@link #writeCompactInt(DataOutput, int)}
     */
    public static int readCompactInt(InputStream pIn) 
        throws IOException
    {
        int result = 0;
        int ch1 = pIn.read();
        
        if (ch1 < 0x40) {
            // 1 byte
            result = ch1;
        } else if ( (ch1 & 0x80) == 0x80 ) {
            // 2 byte
            ch1 &= 0x7F;
            int ch2 = pIn.read();
            result = (ch1 << 8) | (ch2 << 0);
        } else if ( (ch1 & 0x40) == 0x40 ) {
            // 4 byte
            ch1 &= 0x3F;
            int ch2 = pIn.read();
            int ch3 = pIn.read();
            int ch4 = pIn.read();
            result = (ch1 << 24) | (ch2 << 16) | (ch3 << 8) | (ch4 << 0);
        } else {
            // 5 byte
            ch1 = pIn.read();
            int ch2 = pIn.read();
            int ch3 = pIn.read();
            int ch4 = pIn.read();
            result = (ch1 << 24) | (ch2 << 16) | (ch3 << 8) | (ch4 << 0);
        }
        return result;
    }

    /**
     * Get remote interface for some pService class
     */
    public static Class<? extends Remote> getRemote(Class<? extends Remote> pService) {
        Class<? extends Remote> remote = null;
        Class<?> cls = pService;
        
        while (cls != null && remote == null) {
            if (cls.isInterface() && Remote.class.isAssignableFrom(cls)) {
                remote = (Class)cls;
            }
            
            if (remote == null) {
                for (Class interCls : cls.getInterfaces()) {
                    if (Remote.class.isAssignableFrom(interCls)) {
                        remote = interCls;
                        break;
                    }
                }
            }
    
            cls = cls.getSuperclass();
        }
        
        return remote;
    }
    
    
    /**
     * @return always null
     */
    public static <T> T closeSocket(Closeable pSocket) {
        if (pSocket != null) {
            try {
                pSocket.close();
            } catch (Exception e) {
                LOG.warn("Failed to close socket cleanly: " + pSocket, e);
            }
        }
        return null;
    }
    
    /**
     * Create proxy for remote calls
     * 
     * @param pService remote service interface
     */
    public static <T extends Remote> T makeProxy(
            Class<T> pService,
            CallClient pClient,
            CallSessionProvider pSessionProvider) 
        throws IllegalArgumentException,
            InvalidServiceException
    {
        Object service = Proxy.newProxyInstance(
                CallUtil.class.getClassLoader(), 
                new Class[]{pService}, 
                new CallHandler(pService, pClient, pSessionProvider));
        return (T)service;
    }

}
