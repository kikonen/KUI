package org.kari.call;

import java.io.IOException;
import java.net.Socket;
import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.kari.call.event.Call;
import org.kari.call.event.Register;
import org.kari.call.event.Result;

/**
 * Handles communication with one socket in client side
 *
 * @author kari
 */
public final class ClientHandler extends Handler {
    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".client_handler");

    private final ClientKey mClientKey;

    private Object mLastSessionId;


    public ClientHandler(
            Socket pSocket,
            CallClient pClient)
        throws IOException
    {
        super(pSocket,
                pClient.getIOFactory(),
                pClient.isCounterEnabled(),
                pClient.isTraceTrafficStatistics(),
                pClient.isReuseObjectStream(),
                pClient.getCompressThreshold());
        mClientKey = pClient.getClientKey();
    }

    /**
     * <p>NOTE KI precaution against app, which somehow cause failure in
     * proper release() logic of either handler or whole CallClient.
     *
     */
    @Override
    protected void finalize() throws Throwable {
        kill();
        free();
    }

    public ClientKey getClientKey() {
        return mClientKey;
    }

    public Object getLastSessionId() {
        return mLastSessionId;
    }

    public void setLastSessionId(Object pLastSessionId) {
        mLastSessionId = pLastSessionId;
    }

    /**
     * Handshake for asynchronous call handler
     */
    public void handshake() throws Throwable {
        Register.INSTANCE.send(this, mOut);
    }

    /**
     * Invoke call, retrying call is upto caller
     *
     * @throws RetryCallException if retryable call error occurred
     */
    public Object invoke(Call pCall)
        throws
            RetryCallException,
            Throwable
    {
        Result result;

        boolean acked = false;
        try {
            resetByteOut();
            writeEvent(pCall);

            // handle ack
            Result ack = readResult();
            if (ack.getType() != CallType.ACK_CALL_RECEIVED) {
                // error if not ack
                ack.getResult();
                result = null;
            }

            acked = true;
            result = readResult();
        } catch (Throwable e) {
            // suicide; failed write/read socket or invalid result type received
            // thus input is already in invalid state
            // => However, should differentiate phase; if call was sent
            //    succesfully to server, then server may have actually handled it.
            //    In that case transparent "retry" is not possible
            kill();

            if (acked) {
                throw new RemoteException("Failed to access server", e);
            } else {
                throw new RetryCallException("Reconnect", e);
            }
        } finally {
            if (!mRunning) {
                free();
            }

            // discard possible oversized buffer
            resetByteOut();
        }

        return result.getResult();
    }

}

