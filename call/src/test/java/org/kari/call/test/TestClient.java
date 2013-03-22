package org.kari.call.test;

import gnu.trove.set.hash.THashSet;

import java.io.IOException;
import java.net.Socket;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.kari.call.CallClient;
import org.kari.call.CallConstants;
import org.kari.call.CallSessionProvider;
import org.kari.call.CallUtil;
import org.kari.call.ServiceRegistry;
import org.kari.call.event.BufferCall;
import org.kari.call.io.CallClientSocketFactory;
import org.kari.io.DirectByteArrayOutputStream;

/**
 * Test remote call: client side
 *
 * @author kari
 */
public final class TestClient {
    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".test.client");
    public static final boolean COUNTER_ENABLED = true;
    public static final boolean REUSE_STREAM_ENABLED = true;
    public static final int COMPRESS_THRESHOLD = BufferCall.DEFAULT_COMPRESS_THRESHOLD;
    public static final int SO_TIMEOUT = 60 * 1000;
    public static final int IDLE_TIMEOUT = 2 * 60 * 1000;

    public static final boolean TRACE = true;
    public static final boolean TRACE_TRAFFIC_STATISTICS = true;
    public static final boolean RESET_TICKET = false;
    public static final int TEST_COUNT = 1;
    public static final int THREAD_COUNT = 1;


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

    private byte[] mBigBlock;
    private byte[] mRandomHugeBlock;


    private void start(String[] pArgs) throws Exception {
        String host = pArgs.length > 0
            ? pArgs[0]
            : "localhost";

        CallClient call = new CallClient(
                host,
                TestServer.PORT,
                new ServiceRegistry(null),
                new TestIOFactory(),
                mSocketFactory);
        call.setCounterEnabled(COUNTER_ENABLED);
        call.setTraceTrafficStatistics(TRACE_TRAFFIC_STATISTICS);
        call.setReuseObjectStream(REUSE_STREAM_ENABLED);
        call.setCompressThreshold(COMPRESS_THRESHOLD);
        call.setIdleTimeout(IDLE_TIMEOUT);
        call.setCallTimeout(SO_TIMEOUT);

        final Set<Thread> running = new THashSet<Thread>();

        final int testCount = TEST_COUNT;
        final int threadCount = THREAD_COUNT;

        for (int i = 0; i < threadCount; i++) {
            Thread t = new Thread(
                    new TestRunner(call, running, testCount),
                    "test-" + i);
            t.setDaemon(true);
            running.add(t);
        }

        synchronized (running) {
            for (Thread t : running) {
                t.start();
            }
        }

        synchronized (running) {
            while (!running.isEmpty()) {
                running.wait();
            }
        }
    }


    private byte[] getRandomHugeBlock() throws IOException {
        if (mRandomHugeBlock == null) {
            DirectByteArrayOutputStream buffer = new DirectByteArrayOutputStream();
            GZIPOutputStream gout = new GZIPOutputStream(buffer);
            byte[] in = new byte[1000];
            while (buffer.size() < 100000) {
                for (int i = 0; i < in.length; i++) {
                    in[i] = (byte)(Math.random() * 255);
                }
                gout.write(in);
            }
            gout.close();
            mRandomHugeBlock = buffer.toByteArray();
        }
        return mRandomHugeBlock;
    }

    private synchronized byte[] getBigBlock() {
        if (mBigBlock == null) {
            byte[] in = new byte[100000];
            for (int i = 0; i < in.length; i++) {
                in[i] = (byte)(i * 255);
            }
            mBigBlock = in;
        }
        return mBigBlock;
    }


    final class TestRunner implements Runnable {
        private final Set<Thread> mRunning;
        private final TestService mService;
        private final int mTestCount;

        private final ThreadLocal<TestTicket> mTicket = new ThreadLocal<TestTicket>() {
            @Override
            protected TestTicket initialValue() {
                return new TestTicket();
            }
        };


        private final CallSessionProvider mSessionProvider = new CallSessionProvider() {
            @Override
            public Object getSessionId() {
                return mTicket.get();
            }
        };

        public TestRunner(
                CallClient pCall,
                Set<Thread> pThreads,
                int pTestCount)
            throws Exception
        {
            mService = CallUtil.makeProxy(TestService.class, pCall, mSessionProvider);
            mRunning = pThreads;
            mTestCount = pTestCount;
        }

        public void resetTicket() {
            mTicket.set(new TestTicket());
        }

        @Override
        public void run() {
            try {
                synchronized (mRunning) {
                    int x = 0;
                }

                boolean resetTicket = RESET_TICKET;

                for (int i = 0; i < mTestCount; i++) {
                    if (resetTicket) {
                        resetTicket();
                    }
                    runTests(mService);

                    if (i < mTestCount - 1) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                }
            } finally {
                synchronized (mRunning) {
                    mRunning.remove(Thread.currentThread());
                    mRunning.notify();
                }
            }
        }

        private void runTests(TestService service) {
            int idx = 0;
            if (TRACE) LOG.info("TEST " + (idx++) + " - testSimple");
            try {
                TestResult result = service.testSimple(new TestParam("hello"));
                if (TRACE) LOG.info("response: " + result);
            } catch (Exception e) {
                LOG.error("Failed", e);
            }

            if (TRACE) LOG.info("TEST " + (idx++) + " - testSimple-cachedSessionId");
            try {
                TestResult result = service.testSimple(new TestParam("hello"));
                if (TRACE) LOG.info("response: " + result);
            } catch (Exception e) {
                LOG.error("Failed", e);
            }

            if (TRACE) LOG.info("TEST " + (idx++) + " - testVoidParam");
            try {
                TestResult result = service.testVoidParam();
                if (TRACE) LOG.info("response: " + result);
            } catch (Exception e) {
                LOG.error("Failed", e);
            }

            if (TRACE) LOG.info("TEST " + (idx++) + " - testNullResult");
            try {
                TestResult result = service.testNullResult(new TestParam("hello"));
                if (TRACE) LOG.info("response: " + result);
            } catch (Exception e) {
                LOG.error("Failed", e);
            }

            if (TRACE) LOG.info("TEST " + (idx++) + " - testNullParam");
            try {
                TestResult result = service.testNullParam(null);
                if (TRACE) LOG.info("response: " + result);
            } catch (Exception e) {
                LOG.error("Failed", e);
            }

            if (TRACE) LOG.info("TEST " + (idx++) + " - testVoidResult");
            try {
                service.testVoidResult(new TestParam("hello"));
                if (TRACE) LOG.info("response: VOID");
            } catch (Exception e) {
                LOG.error("Failed", e);
            }

            if (TRACE) LOG.info("TEST " + (idx++) + " - bigCall");
            try {
                byte[] in = getBigBlock();
                byte[] out = service.testBigCall(in);
                if (TRACE) LOG.info("response: " + out.length);
            } catch (Exception e) {
                LOG.error("Failed", e);
            }

            if (TRACE) LOG.info("TEST " + (idx++) + " - bigCall");
            try {
                byte[] buffer = getRandomHugeBlock();
                byte[] out = service.testBigCall(buffer);
                if (TRACE) LOG.info("response: " + out.length);
            } catch (Exception e) {
                LOG.error("Failed", e);
            }

            if (TRACE) LOG.info("TEST " + (idx++) + " - testError");
            try {
                TestResult result = service.testError(new TestParam("hello"));
                if (TRACE) LOG.info("response: " + result);
            } catch (TestException e) {
                if (TRACE) LOG.info("Expected Error", e);
            } catch (Exception e) {
                LOG.error("Failed", e);
            }
        }
    }
}
