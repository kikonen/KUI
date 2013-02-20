package org.kari.call.test;

import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.kari.call.CallClient;
import org.kari.call.CallConstants;
import org.kari.call.CallSessionProvider;
import org.kari.call.CallUtil;
import org.kari.call.ServiceRegistry;
import org.kari.call.io.CallClientSocketFactory;

/**
 * Test remote call: client side
 *
 * @author kari
 */
public final class TestClient {
    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".test.client");
    
    public static void main(String[] args) {
        try {
            BasicConfigurator.configure();
            new TestClient().start(args);
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
    
    private void start(String[] pArgs) throws Exception {
        String host = pArgs.length > 0 
            ? pArgs[0]
            : "localhost";
            
        CallClient client = new CallClient(
                host, 
                TestServer.PORT, 
                mSocketFactory,
                new TestIOFactory(),
                new ServiceRegistry(null));
    
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
