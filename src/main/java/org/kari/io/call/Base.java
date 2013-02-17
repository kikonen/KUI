package org.kari.io.call;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.kari.io.CompactObjectInputStream;
import org.kari.io.CompactObjectOutputStream;


/**
 * Shared logic between call and result
 *
 * @author kari
 */
public abstract class Base {
    /**
     * Is buffer or stream call used
     */
    public static final boolean BUFFER_CALL = true;
    
    /**
     * Compression is preferred in out/in
     */
    public static final boolean COMPRESS = false;
    
    /**
     * "no desc" out/in object streams preferred
     */
    public static final boolean NO_DESC = true;

    private final CallType mType;

    
    /**
     * @see #COMPRESS
     * @see #NO_DESC
     */
    public static ObjectOutputStream createObjectOut(OutputStream pOut)
            throws IOException 
    {
        return createObjectOut(pOut, COMPRESS);
    }
    
    public static ObjectOutputStream createObjectOut(
            final OutputStream pOut,
            final boolean pCompressed)
        throws IOException 
    {
        OutputStream out = pCompressed
            ? new GZIPOutputStream(pOut, true) 
            : pOut;
        return NO_DESC
            ? new CompactObjectOutputStream(out)
            : new ObjectOutputStream(out);
    }
    
    /**
     * @see #COMPRESS
     * @see #NO_DESC
     */
    public static ObjectInputStream createObjectInput(InputStream pIn)
            throws IOException 
    {
        return createObjectInput(pIn, COMPRESS);
    }
    
    public static ObjectInputStream createObjectInput(
            final InputStream pIn,
            final boolean pCompressed)
        throws IOException 
    {
        InputStream in = pCompressed
            ? new GZIPInputStream(pIn)
            : pIn;
        return NO_DESC
            ? new CompactObjectInputStream(in)
            : new ObjectInputStream(in);
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

    
    protected Base(CallType pType) {
        mType = pType;
    }
    
    public final CallType getType() {
        return mType;
    }
    
    /**
     * Write type and call {@link #write(DataOutputStream)}
     */
    public final void send(Handler pHandler, DataOutputStream pOut) 
        throws Exception
    {
        pHandler.getBuffer().reset();
        pOut.write(mType.mCode);
        write(pHandler, pOut);
        
        pOut.flush();
    }
    
    /**
     * Read header and call {@link #read(DataOutputStream)}
     */
    public final void receive(Handler pHandler, DataInputStream pIn) 
        throws Exception
    {
        pHandler.getBuffer().reset();
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
