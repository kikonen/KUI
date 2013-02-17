package org.kari.io.call;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.kari.io.DirectByteArrayOutputStream;

/**
 * Remote call using intermediate buffer
 *
 * @author kari
 */
public final class BufferCall extends ServiceCall {
    public static final int COMPRESS_THRESHOLD = 100;
    
    /**
     * For decoding call
     */
    public BufferCall() {
        super(CallType.BUFFER_CALL);
    }
    
    /**
     * @param pSessionId Identifies session for session authentication, 
     * can be null
     * @param pParams null if no params
     */
    public BufferCall(
            Object pSessionId,
            boolean pSessionIdChanged,
            int pServiceUUID, 
            long pMethodId,
            Object[] pParams) 
    {
        super(CallType.BUFFER_CALL, pSessionId, pSessionIdChanged, pServiceUUID, pMethodId, pParams);
    }
    
    @Override
    protected void write(Handler pHandler, DataOutputStream pOut) 
        throws Exception
    {
        DirectByteArrayOutputStream buffer = pHandler.getBuffer();
        ObjectOutputStream oo = createObjectOut(buffer, false);
        write(oo);
        oo.flush();
        
        writeBuffer(buffer, pOut);
    }
    
    @Override
    protected void read(Handler pHandler, DataInputStream pIn) 
        throws IOException,
            ClassNotFoundException
    {
        DirectByteArrayOutputStream buffer = readBuffer(pHandler, pIn);

        ObjectInputStream oi = createObjectInput(
                new ByteArrayInputStream(
                        buffer.getBuffer(),  
                        0,  
                        buffer.size()),
                false);
        
        read(oi);
    }

    protected static void writeBuffer(
            final DirectByteArrayOutputStream buffer,
            final DataOutputStream pOut)
            throws IOException 
    {
        final int count = buffer.size();
        final boolean compressed = count > BufferCall.COMPRESS_THRESHOLD;
        
        pOut.writeBoolean(compressed);
        pOut.writeInt(count);
        
        OutputStream out = pOut;
        if (compressed) {
            out = new GZIPOutputStream(out, true);
        }

        out.write(buffer.getBuffer(), 0, count);

        // ensure GZIP is properly finished
        if (compressed) {
            out.flush();
        }
    }

    /**
     * Read data from pIn to buffer
     */
    protected static DirectByteArrayOutputStream readBuffer(
            Handler pHandler,
            DataInputStream pIn) 
        throws IOException 
    {
        final DirectByteArrayOutputStream buffer = pHandler.getBuffer();
        boolean compressed = pIn.readBoolean();
        int count = pIn.readInt();

        
        // read data
        {
            InputStream in = pIn;
            if (compressed) {
                in = new GZIPInputStream(in);
            }

            byte[] data = pHandler.getDataBuffer();
            int read = 0;
            while (read < count) {
                int n = in.read(data, 0, Math.min(count - read, data.length));
                buffer.write(data, 0, n);
                read += n;
            }
        }
        return buffer;
    }

    @Override
    protected Result createResult(Object pResult) {
        return new BufferResult(pResult);
    }

}
