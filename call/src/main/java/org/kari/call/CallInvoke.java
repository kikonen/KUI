package org.kari.call;

import org.apache.log4j.Logger;
import org.kari.call.event.Call;
import org.kari.call.event.ErrorResult;
import org.kari.call.event.Result;


/**
 * Handle actual call and dispatch results back using some available
 * ServerHandler
 *
 * @author kari
 */
public final class CallInvoke
    implements
        Runnable
{
    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".executor");

    private final ClientKey mKey;
    private final CallServer mServer;
    private final Call mCall;

    public CallInvoke(
            CallServer pServer,
            ClientKey pKey,
            Call pCall)
    {
        mServer = pServer;
        mKey = pKey;
        mCall = pCall;
    }

    @Override
    public void run() {
        Result result = null;

        try {
            // execute after sending ack
            result = mCall.invoke(
                    mServer.getRegistry(),
                    mServer.getCallInvoker());
        } catch (Throwable e) {
            result = new ErrorResult(e);
            // normal call failure
        }

        ConnectionPool pool = mServer.getConnectionPool();
        ServerHandler conn = pool.reserve(mKey);
        try {
            conn.writeEvent(result);
        } catch (Exception e) {
            LOG.error("Failed to send result", e);
            result.traceDebug();
        } finally {
            pool.release(conn);
        }
    }

}
