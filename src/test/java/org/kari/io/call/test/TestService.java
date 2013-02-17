package org.kari.io.call.test;

import java.rmi.Remote;
import java.rmi.RemoteException;

import org.kari.io.call.ServiceName;


@ServiceName(name="test", id=1)
public interface TestService extends Remote {
    void testVoidResult(TestParam pParam)
        throws
            RemoteException;

    TestResult testNullResult(TestParam pParam)
            throws
                RemoteException;

    TestResult testNullParam(TestParam pParam)
            throws
                RemoteException;

    TestResult testVoidParam()
        throws
            RemoteException;

    TestResult testSimple(TestParam pParam)
        throws
            RemoteException;

    TestResult testError(TestParam pParam)
        throws
            RemoteException,
            TestException;
}
