package org.kari.call.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.kari.call.CallConstants;
import org.kari.call.CallServer;
import org.kari.call.ServiceRegistry;
import org.kari.call.io.CallServerSocketFactory;

/**
 * Test remote call: server side
 *
 * @author kari
 */
public final class TestServer {
    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".test.server");
    
    static final int PORT = 8100;
    
    private final CallServerSocketFactory mSocketFactory = new CallServerSocketFactory() {
        @Override
        public ServerSocket createSocket(String pServerAddress, int pPort)
            throws IOException 
        {
            InetAddress address = pServerAddress != null
                ? InetAddress.getByName(pServerAddress)
                : null;
            return new ServerSocket(pPort, 50, address);
        }
    };
    
    public static void main(String[] args) {
        try {
            BasicConfigurator.configure();
            new TestServer().start();
        } catch (Exception e) {
            LOG.error("server failed", e);
            System.exit(-1);
        }
    }
    
    private void start() throws Exception {
        CallServer server = new CallServer(
                null, 
                PORT, 
                mSocketFactory, 
                new TestIOFactory(),
                new ServiceRegistry());
        
        server.getRegistry().register(new TestServiceImpl());
    
        server.start();
        LOG.info("started server");
    }

}
