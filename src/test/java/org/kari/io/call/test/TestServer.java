package org.kari.io.call.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

import org.apache.log4j.Logger;
import org.kari.io.call.CallConstants;
import org.kari.io.call.CallServer;
import org.kari.io.call.CallServerSocketFactory;
import org.kari.io.call.ServiceRegistry;
import org.kari.util.log.LogUtil;

/**
 * Test remote call: server side
 *
 * @author kari
 */
public final class TestServer {
    private static final Logger LOG = LogUtil.getLogger(CallConstants.BASE_PKG + ".test.server");
    
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
                new ServiceRegistry());
        
        server.getRegistry().register(new TestServiceImpl());
    
        server.start();
        LOG.info("started server");
    }

}
