package org.kari.io.call.test;

import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.kari.io.call.CallClient;
import org.kari.io.call.CallClientSocketFactory;
import org.kari.io.call.CallConstants;
import org.kari.io.call.CallSessionProvider;
import org.kari.io.call.CallUtil;
import org.kari.io.call.ServiceRegistry;
import org.kari.util.log.LogUtil;

/**
 * Test remote call: client side
 *
 * @author kari
 */
public final class TestClient {
    private static final Logger LOG = LogUtil.getLogger(CallConstants.BASE_PKG + ".test.client");
    
    public static void main(String[] args) {
        try {
            new TestClient().start();
        } catch (Exception e) {
            LOG.error("client failed", e);
            System.exit(-1);
        }
    }
    
    private final CallClientSocketFactory mSocketFactory = new CallClientSocketFactory() {
        @Override
        public Socket createSocket(String pServerAddress, int pPort)
            throws IOException 
        {
            return new Socket(pServerAddress, pPort);
        }
    };

    TestTicket mTicket = new TestTicket();

    private final CallSessionProvider mSessionProvider = new CallSessionProvider() {
        @Override
        public Object getSessionId() {
            return mTicket;
        }
    };
    
    public void resetTicket() {
        mTicket = new TestTicket();
    }
    
    private void start() throws Exception {
        CallClient client = new CallClient(
                "localhost", 
                TestServer.PORT, 
                mSocketFactory,
                new ServiceRegistry());
    
        TestService service = CallUtil.makeProxy(TestService.class,  client, mSessionProvider);
    
        int count = 1;
        boolean reset = false;
        
        for (int i = 0; i < count; i++) {
            if (reset) {
                resetTicket();
            }
            runTests(service);
        }
    }

    private void runTests(TestService service) {
        int idx = 0;
        LOG.info("TEST " + (idx++) + " - testSimple");
        try {
            TestResult result = service.testSimple(new TestParam("hello"));
            LOG.info("response: " + result);    
        } catch (Exception e) {
            LOG.error("Failed", e);
        }

        LOG.info("TEST " + (idx++) + " - testSimple-cachedSessionId");
        try {
            TestResult result = service.testSimple(new TestParam("hello"));
            LOG.info("response: " + result);    
        } catch (Exception e) {
            LOG.error("Failed", e);
        }

        LOG.info("TEST " + (idx++) + " - testVoidParam");
        try {
            TestResult result = service.testVoidParam();
            LOG.info("response: " + result);    
        } catch (Exception e) {
            LOG.error("Failed", e);
        }
    
        LOG.info("TEST " + (idx++) + " - testNullResult");
        try {
            TestResult result = service.testNullResult(new TestParam("hello"));
            LOG.info("response: " + result);    
        } catch (Exception e) {
            LOG.error("Failed", e);
        }
    
        LOG.info("TEST " + (idx++) + " - testNullParam");
        try {
            TestResult result = service.testNullParam(null);
            LOG.info("response: " + result);    
        } catch (Exception e) {
            LOG.error("Failed", e);
        }
    
        LOG.info("TEST " + (idx++) + " - testVoidResult");
        try {
            service.testVoidResult(new TestParam("hello"));
            LOG.info("response: VOID");    
        } catch (Exception e) {
            LOG.error("Failed", e);
        }

        LOG.info("TEST " + (idx++) + " - bigCall");
        try {
            byte[] in = new byte[100000];
            for (int i = 0; i < in.length; i++) {
                in[i] = (byte)i;
            }
            byte[] out = service.testBigCall(in);
            LOG.info("response: " + out.length);    
        } catch (Exception e) {
            LOG.error("Failed", e);
        }

        LOG.info("TEST " + (idx++) + " - testError");
        try {
            TestResult result = service.testError(new TestParam("hello"));
            LOG.info("response: " + result);    
        } catch (Exception e) {
            LOG.error("Expected Error", e);
        }
    }

}
