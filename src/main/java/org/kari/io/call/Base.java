package org.kari.io.call;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
     * Compression is preferred in out/in
     */
    public static final boolean COMPRESS = true;
    /**
     * "no desc" out/in object streams preferred
     */
    public static final boolean NO_DESC = true;

    private final CallType mType;

    
    /**
     * @see #COMPRESS
     * @see #NO_DESC
     */
    public static ObjectOutputStream createObjectOut(DataOutputStream pOut)
            throws IOException 
    {
        FilterOutputStream out = COMPRESS 
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
    public static ObjectInputStream createObjectInput(DataInputStream pIn)
            throws IOException 
    {
        FilterInputStream in = COMPRESS
            ? new GZIPInputStream(pIn)
            : pIn;
        return NO_DESC
            ? new CompactObjectInputStream(in)
            : new ObjectInputStream(in);
    }
    
    
    public Base(CallType pType) {
        mType = pType;
    }
    
    public final CallType getType() {
        return mType;
    }
    
    /**
     * Write type and call {@link #write(DataOutputStream)}
     */
    public final void send(DataOutputStream pOut) 
        throws Exception
    {
        pOut.write(mType.mCode);
        write(pOut);
        
        pOut.flush();
    }
    
    /**
     * Read header and call {@link #read(DataOutputStream)}
     */
    public final void receive(DataInputStream pIn) 
        throws Exception
    {
        read(pIn);
    }
    
    /**
     * Write to stream. No need to call flush, send() will trigger it
     */
    protected abstract void write(DataOutputStream pOut) 
        throws Exception;
    
    /**
     * read from stream
     */
    protected abstract void read(DataInputStream pIn) 
        throws IOException,
            ClassNotFoundException;
    
}
