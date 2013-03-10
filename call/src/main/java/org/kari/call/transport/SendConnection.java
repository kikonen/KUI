package org.kari.call.transport;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.kari.call.io.CountOutputStream;


/**
 * Send only connection
 *
 * @author kari
 */
public final class SendConnection extends Connection {
    protected final CountOutputStream mCountOut;
    protected final DataOutputStream mOut;

    public SendConnection(
            TransportKey pKey,
            Socket pSocket,
            boolean pCounterEnabled)
        throws IOException
    {
        super(pKey, pSocket, pCounterEnabled);

        if (pCounterEnabled) {
            mCountOut = new CountOutputStream(mSocket.getOutputStream());
            mOut = new DataOutputStream(new BufferedOutputStream(mCountOut));
        } else {
            mCountOut = null;
            mOut = new DataOutputStream(new BufferedOutputStream(mSocket.getOutputStream()));
        }

        mSocket.getInputStream().close();
    }

}
