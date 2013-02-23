package org.kari.call.test;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.kari.call.CallConstants;


public final class TestServiceImpl implements TestService {
    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".test.service");
    private static final boolean TRACE = TestClient.TRACE;
    
    @Override
    public void testVoidResult(TestParam pParam) throws RemoteException {
        if (TRACE) LOG.info("voidResult: " + pParam);
    }

    @Override
    public TestResult testNullResult(TestParam pParam) throws RemoteException {
        if (TRACE) LOG.info("nullResult: " + pParam);
        return null;
    }

    @Override
    public TestResult testNullParam(TestParam pParam) throws RemoteException {
        if (TRACE) LOG.info("nullParam: " + pParam);
        return new TestResult(pParam);
    }

    @Override
    public TestResult testVoidParam() throws RemoteException {
        if (TRACE) LOG.info("voidParam:");
        return new TestResult("void-param");
    }

    @Override
    public TestResult testSimple(TestParam pParam) throws RemoteException {
        if (TRACE) LOG.info("simple: " + pParam);
        return new TestResult(pParam);
    }

    @Override
    public TestResult testError(TestParam pParam) 
        throws RemoteException,
            TestException 
    {
        if (TRACE) LOG.info("error: " + pParam);
        throw new TestException("err-" + pParam);
    }

    @Override
    public byte[] testBigCall(byte[] pData) throws RemoteException {
        if (TRACE) LOG.info("big: " + pData.length);
        return pData;
    }
    
}
