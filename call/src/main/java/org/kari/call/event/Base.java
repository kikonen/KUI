package org.kari.call.event;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import org.kari.call.CallType;
import org.kari.call.Handler;


/**
 * Shared logic between call and result
 *
 * @author kari
 */
public abstract class Base {
    static {
        CallType.initCache();
    }

    /**
     * Read data fully from pIn
     */
    public static void readFully(
            InputStream pIn,
            byte[] pBuffer,
            int pOffset,
            int pLen)
        throws IOException
    {
        if (pLen < 0) {
            throw new IndexOutOfBoundsException();
        }
        int n = 0;
        while (n < pLen) {
            int count = pIn.read(pBuffer, pOffset + n, pLen - n);
            if (count < 0) {
                throw new EOFException();
            }
            n += count;
        }
    }


    protected Base() {
        // nothing
    }

    public abstract CallType getType();

    /**
     * Write type and call {@link #write(DataOutputStream)}
     */
    public final void send(Handler pHandler, DataOutputStream pOut)
        throws Exception
    {
        pHandler.getByteOut().reset();

        pOut.write(getType().mCode);
        write(pHandler, pOut);

        pOut.flush();
    }

    /**
     * Read header and call {@link #read(DataOutputStream)}
     */
    public final void receive(Handler pHandler, DataInputStream pIn)
        throws Exception
    {
        pHandler.getByteOut().reset();
        read(pHandler, pIn);
    }

    /**
     * Write to stream. No need to call flush, send() will trigger it
     */
    protected abstract void write(Handler pHandler, DataOutputStream pOut)
        throws Exception;

    /**
     * read from stream
     */
    protected abstract void read(Handler pHandler, DataInputStream pIn)
        throws IOException,
            ClassNotFoundException;

}
