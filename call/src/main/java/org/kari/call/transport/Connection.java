package org.kari.call.transport;

import java.io.IOException;
import java.net.Socket;

import org.kari.call.TransferCounter;


/**
 * Connection: either receice or send. Separate connections are used for
 * sending and receiving, due to asynchronous call dispatch logic
 *
 * @author kari
 */
public abstract class Connection {
    protected final TransportKey mKey;
    protected final Socket mSocket;

    protected final boolean mCounterEnabled;
    protected final TransferCounter mCounter;


    protected Connection(
            TransportKey pKey,
            Socket pSocket,
            boolean pCounterEnabled)
        throws IOException
    {
        mKey = pKey;
        mSocket = pSocket;

        mCounterEnabled = pCounterEnabled;
        if (pCounterEnabled) {
            mCounter = TransferCounter.INSTANCE;
        } else {
            mCounter = null;
        }
    }

    public TransportKey getKey() {
        return mKey;
    }
}
