package org.kari.call.transport;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import org.kari.call.io.CountInputStream;

/**
 * Receive only connection
 *
 * @author kari
 */
public class ReceiveConnection extends Connection {
    protected final CountInputStream mCountIn;
    protected final DataInputStream mIn;


    public ReceiveConnection(
            TransportKey pKey,
            Socket pSocket,
            boolean pCounterEnabled)
        throws IOException
    {
        super(pKey, pSocket, pCounterEnabled);

        if (pCounterEnabled) {
            mCountIn = new CountInputStream(mSocket.getInputStream());
            mIn = new DataInputStream(new BufferedInputStream(mCountIn));
        } else {
            mCountIn = null;
            mIn = new DataInputStream(new BufferedInputStream(mSocket.getInputStream()));
        }

        mSocket.getOutputStream().close();
    }

    public CountInputStream getCountIn() {
        return mCountIn;
    }

    public DataInputStream getIn() {
        return mIn;
    }
}
