package org.kari.call;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.kari.call.event.Call;

/**
 * Pool for reused actual call handlers.
 *
 * <p>TODO KI Is pool really needed; these are really simple objects?!?
 *
 * @author kari
 */
public final class CallExecutor
{
    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".executor");

    private final CallServer mServer;
    private final ExecutorService mExecutor;

    public CallExecutor(CallServer pServer) {
        mServer = pServer;
        mExecutor = Executors.newCachedThreadPool();
    }

    /**
     * Add pCall from pHandler for execution
     */
    public void execute(ServerHandler pHandler, Call pCall) {
        CallInvoke invoke = new CallInvoke(
                mServer,
                pHandler.getClientKey(),
                pCall);
        mExecutor.execute(invoke);
    }

}
