package org.kari.io.call;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.apache.log4j.Logger;
import org.kari.io.CountInputStream;
import org.kari.io.CountOutputStream;

/**
 * Handles all incoming in one socket
 *
 * @author kari
 */
public final class ServerHandler extends Thread {
    private static final Logger LOG = Logger.getLogger(CallConstants.BASE_PKG + ".server_handler");
    
    private final CallServer mServer;
    
    private final Socket mSocket;

    private final CountOutputStream mCountOut;
    private final CountInputStream mCountIn;

    private final DataInputStream mIn;
    private final DataOutputStream mOut;
    
    private volatile boolean mRunning = true;

    private Object mLastSessionId;
    
    public ServerHandler(CallServer pServer, Socket pSocket) throws IOException {
        super("ServerHandler-" + pSocket);
        setDaemon(true);
    
        mServer = pServer;
        mSocket = pSocket;
        mCountOut = new CountOutputStream(mSocket.getOutputStream());
        mCountIn = new CountInputStream(mSocket.getInputStream());
        mIn = new DataInputStream(new BufferedInputStream(mCountIn));
        mOut = new DataOutputStream(new BufferedOutputStream(mCountOut));
    }
    
    public void kill() {
        mRunning = false;
        CallUtil.closeSocket(mSocket);
    }

    public boolean isRunning() {
        return mRunning && mServer.isRunning();
    }
    
    @Override
    public void run() {
        boolean waiting = true;
        try {
            while (isRunning()) {
                mCountOut.markCount();
                mCountIn.markCount();
                try {
                    waiting = true;
                    int code = mIn.read();
                    waiting = false;
                    
                    // handle call only if server is still running
                    if (isRunning()) {
                        CallType type = CallType.resolve(code);
                        handle(type);
                    }
                } finally {
                    if (true) {
                        LOG.info("out=" + mCountOut.getMarkSize() + ", in=" + mCountIn.getMarkSize());
                    }
                }
            }
        } catch (Exception e) {
            if (!waiting) {
                LOG.error("handler failed", e);
            }
        } finally {
            kill();
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
            result = new ErrorResult(e);
            // cleanup by enforcing socket re-create; state unrecoversable
            // since it's not possible to know how to read data for unsupported
            // protocol
            suicide = true;
        }
        
        if (call != null) {
            try {
                call.receive(mIn);
                mLastSessionId = call.getSessionId();
            } catch (Throwable e) {
                result = new ErrorResult(e);
                // socket has failed or major internal error
                // => Attempt to send error to client and die
                suicide = true;
            }
        
            try {
                result = call.invoke(mServer.getRegistry());
            } catch (Throwable e) {
                result = new ErrorResult(e);
                // normal call failure
            }
        }
        
        try {
            result.send(mOut);
        } catch (Exception e) {
            LOG.error("Failed to send result", e);
            result.traceDebug();
        } finally {
            // kill server hand let client reconnect
            if (suicide) {
                kill();
            }
        }
    }
    
}
