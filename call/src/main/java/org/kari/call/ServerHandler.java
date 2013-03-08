package org.kari.call;

import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.kari.call.event.AckCallReceived;
import org.kari.call.event.Call;
import org.kari.call.event.ErrorResult;
import org.kari.call.event.Result;

/**
 * Handles all incoming in one socket
 *
 * @author kari
 */
public final class ServerHandler extends Handler 
    implements
        Runnable
{
    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".server_handler");
    private static final int PROTOCOL_STACK_SKIP = 2;
    private static final int INVOKE_STACK_SKIP = 3;
    
    private final CallServer mServer;
    
    private Thread mThread;
    private Object mLastSessionId;

    
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
        final boolean TRACE = mTraceTrafficStatistics;
        boolean waiting = true;
        try {
            while (isRunning()) {
                if (mCounterEnabled) {
                    mCountOut.markCount();
                    mCountIn.markCount();
                }

                boolean success = false;
                try {
                    success = false;
                    waiting = true;
                    int code = mIn.read();
                    waiting = false;
                    
                    if (code < 0) {
                        // EOF
                        mRunning = false;
                    } else {
                        // handle call only if server is still running
                        if (isRunning()) {
                            CallType type = CallType.resolve(code);
                            handle(type);
                        }
                        success = true;
                    }
                } finally {
                    if (mCounterEnabled && success) {
                        if (TRACE) LOG.info("out=" + mCountOut.getMarkSize() + ", in=" + mCountIn.getMarkSize());
                        mCounter.add(mCountOut.getMarkSize(), mCountIn.getMarkSize());
                    }
                }
            }
        } catch (Exception e) {
            if (!waiting) {
                LOG.error("handler failed", e);
            }
        } finally {
            if (mCounterEnabled) {
                synchronized (mCounter) {
                    LOG.info("totalOut=" + mCounter.getOutBytes() + ", totalIn=" + mCounter.getInBytes() + ", calls=" + mCounter.getCalls());
                }
            }
            kill();
            free();
        }
    }

    private void handle(CallType pType) {
        boolean suicide = false;
        Result result = null;
        Call call = null;
        try {
            call = (Call)pType.create();
            call.setSessionId(mLastSessionId);
        } catch (Throwable e) {
            result = new ErrorResult(e, PROTOCOL_STACK_SKIP);
            // cleanup by enforcing socket re-create; state unrecoversable
            // since it's not possible to know how to read data for unsupported
            // protocol
            suicide = true;
        }
        
        if (call != null) {
            boolean received = false;
            boolean acked = false;
            try {
                resetByteOut();
                call.receive(this, mIn);
                mLastSessionId = call.getSessionId();
                received = true;
            } catch (Throwable e) {
                result = new ErrorResult(e, PROTOCOL_STACK_SKIP);
                // socket has failed or major internal error
                // => Attempt to send error to client and die
                suicide = true;
            } finally {
                // discard possible oversized buffer
                resetByteOut();
            }

            if (received) {
                try {
                    AckCallReceived.INSTANCE.send(this, mOut);
                    acked = true;
                } catch (Throwable e) {
                    result = new ErrorResult(e, PROTOCOL_STACK_SKIP);
                    // Socket may be unstable; restart
                    suicide = true;
                }
            }
            
            if (acked) {
                try {
                    // execute after sending ack
                    result = call.invoke(
                            mServer.getRegistry(),
                            mServer.getCallInvoker());
                } catch (Throwable e) {
                    result = new ErrorResult(e, INVOKE_STACK_SKIP);
                    // normal call failure
                }
            }
        }
        
        try {
            result.send(this, mOut);
        } catch (Exception e) {
            LOG.error("Failed to send result", e);
            result.traceDebug();
        } finally {
            // kill server hand let client reconnect
            if (suicide) {
                // soft server kindly; after next iteration in while to allow
                // client some time to receive results
                mRunning = false;
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // ignore; dying anyway
                }
            }
        }
    }
    
}
