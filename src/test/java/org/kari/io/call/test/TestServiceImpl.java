package org.kari.io.call.test;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.kari.io.call.CallConstants;

public class TestServiceImpl implements TestService {
    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".test.service");

    @Override
    public void testVoidResult(TestParam pParam) throws RemoteException {
        LOG.info("voidResult: " + pParam);
    }

    @Override
    public TestResult testNullResult(TestParam pParam) throws RemoteException {
        LOG.info("nullResult: " + pParam);
        return null;
    }

    @Override
    public TestResult testNullParam(TestParam pParam) throws RemoteException {
        LOG.info("nullParam: " + pParam);
        return new TestResult(pParam);
    }

    @Override
    public TestResult testVoidParam() throws RemoteException {
        LOG.info("voidParam:");
        return new TestResult("void-param");
    }

    @Override
    public TestResult testSimple(TestParam pParam) throws RemoteException {
        LOG.info("simple: " + pParam);
        return new TestResult(pParam);
    }

    @Override
    public TestResult testError(TestParam pParam) 
        throws RemoteException,
            TestException 
    {
        LOG.info("error: " + pParam);
        throw new TestException("err-" + pParam);
    }

    @Override
    public byte[] testBigCall(byte[] pData) throws RemoteException {
        LOG.info("big: " + pData.length);
        return pData;
    }
    
}
