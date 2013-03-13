package org.kari.call;

import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.kari.call.event.AckCallReceived;
import org.kari.call.event.AsyncRegister;
import org.kari.call.event.Call;
import org.kari.call.event.CallEvent;
import org.kari.call.event.ErrorResult;
import org.kari.call.event.Result;

/**
 * Waits for incoming calls and dispatches them to ServerCallHandler
 *
 * @author kari
 *
 * @see CallInvoke
 */
public final class ServerHandler extends Handler
    implements
        Runnable
{
    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".server_handler");


    private final CallServer mServer;
    private ClientKey mClientKey;

    private Thread mThread;


    public ServerHandler(CallServer pServer, Socket pSocket) throws IOException {
        super(pSocket,
                pServer.getIOFactory(),
                pServer.isCounterEnabled(),
                pServer.isTraceTrafficStatistics(),
                pServer.isReuseObjectStream(),
                pServer.getCompressThreshold());

        mServer = pServer;
    }

    /**
     * @return key if async result sender, null otherwise
     */
    public ClientKey getClientKey() {
        return mClientKey;
    }

    /**
     * Start handler thread
     */
    public void start() {
        if (isRunning()) {
            synchronized (this) {
                mThread = new Thread(this, "Handler-" + mSocket.getRemoteSocketAddress()+ "-" + mSocket.getLocalPort());
                mThread.setDaemon(true);
                mThread.start();
            }
        }
    }

    @Override
    public void kill() {
        super.kill();

        synchronized (this) {
            Thread thread = mThread;
            mThread = null;
            if (thread != null) {
                thread.interrupt();
            }
        }
    }

    @Override
    public boolean isRunning() {
        return super.isRunning() && mServer.isRunning();
    }

    @Override
    public void run() {
        boolean async = handshake();
        if (async) {
            // if async then stop thread; connection is left into ConnectionPool
            return;
        }

        try {
            while (isRunning()) {
                dispatchEvent();
            }
        } catch (Throwable e) {
            if (e != EOF_EXCEPTION) {
                LOG.error("handler failed", e);
            }
        } finally {
            kill();
            free();

            if (mCounterEnabled) {
                synchronized (mCounter) {
                    LOG.info("totalOut=" + mCounter.getOutBytes()
                            + ", totalIn=" + mCounter.getInBytes()
                            + ", eventsOut=" + mCounter.getOutEvents()
                            + ", eventsIn=" + mCounter.getInEvents());
                }
            }
        }
    }

    private void dispatchEvent() throws EOFException, Exception {
        Call call = null;

        try {
            call = readCall();
        } catch (Exception e) {
            // socket died; kill server
            mRunning = false;
            throw EOF_EXCEPTION;
        }

        try {
            writeEvent(AckCallReceived.INSTANCE);

            if (isRunning()) {
                handle(call);
            }
        } catch (EOFException e) {
            // socket closed; just die silently
            throw e;
        } catch (Throwable e) {
            // protocol error; client send likely corrupted event
            // => kill server hand let client reconnect
            LOG.debug("socket closed", e);

            mRunning = false;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException _) {
                // ignore; dying anyway
            }
        } finally {
            // discard possible oversized buffer
            resetByteOut();
        }
    }

    /**
     * Handshake with client to establish normal handler vs. async result
     * transfer socket registration
     *
     * @return true if async result sending socket
     */
    private boolean handshake() {
        boolean result = false;
        boolean success = false;
        try {
            CallEvent event = readEvent();
            if (event instanceof AsyncRegister) {
                result = true;
                AsyncRegister async = (AsyncRegister)event;
                mClientKey = async.getResult();
                mServer.getConnectionPool().register(this);
            }
            success = true;
        } catch (EOFException e) {
            // ok; silent exit client closed connnection
        } catch (Exception e) {
            try {
                writeEvent(new ErrorResult(e));
            } catch (Exception e2) {
                LOG.debug("Protocol error", e);
                LOG.debug("Failed to report error to client", e2);
            }
        } finally {
            if (!success) {
                kill();
                free();
            }
        }
        return result;
    }

    private void handle(Call pCall) throws Throwable {
        Result result = null;
        try {
            // execute after sending ack
            result = pCall.invoke(
                    mServer.getRegistry(),
                    mServer.getCallInvoker());
        } catch (Throwable e) {
            // normal call failure
            result = new ErrorResult(e);
        }

        try {
            writeEvent(result);
        } catch (EOFException e) {
            throw EOF_EXCEPTION;
        } catch (Exception e) {
            // protocol failure; die
            LOG.debug("failed to send result to client", e);
            result.traceDebug();

            throw EOF_EXCEPTION;
        }
    }

}
