package org.kari.call.test;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.rmi.Remote;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.kari.call.CallConstants;
import org.kari.call.CallInvoker;
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
    private static final boolean TRACE = TestClient.TRACE;
    private static final boolean COUNTER_ENABLED = TestClient.COUNTER_ENABLED;
    private static final boolean TRACE_TRAFFIC_STATISTICS = TestClient.TRACE_TRAFFIC_STATISTICS;
    private static final boolean REUSE_STREAM_ENABLED = TestClient.REUSE_STREAM_ENABLED;
    public static final int COMPRESS_THRESHOLD = TestClient.COMPRESS_THRESHOLD;
    public static final int SO_TIMEOUT = 30000;
    public static final int IDLE_TIMEOUT = 60000;

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

    private final CallInvoker mInvoker = new CallInvoker() {
        @Override
        public Object invoke(
                Object pSessionId,
                Remote pService,
                Method pMethod,
                Object[] pParams)
            throws Throwable
        {
            if (TRACE) LOG.info("session: " + pSessionId);
            try {
                return pMethod.invoke(pService, pParams);
            } finally {
                // nothing
            }
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
        CallServer call = new CallServer(
                null,
                PORT,
                new ServiceRegistry(null),
                new TestIOFactory(),
                mSocketFactory,
                mInvoker);
        call.setCounterEnabled(COUNTER_ENABLED);
        call.setTraceTrafficStatistics(TRACE_TRAFFIC_STATISTICS);
        call.setReuseObjectStream(REUSE_STREAM_ENABLED);
        call.setCompressThreshold(COMPRESS_THRESHOLD);
        call.setIdleTimeout(IDLE_TIMEOUT);
        call.setCallTimeout(SO_TIMEOUT);

        call.getRegistry().register(new TestServiceImpl());

        // not daemon since it it's only thread
        call.start(false);

        LOG.info("started server");
    }

}
