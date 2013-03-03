package org.kari.call.event;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.kari.call.CallType;
import org.kari.call.CallUtil;
import org.kari.call.Handler;
import org.kari.call.io.DirectByteArrayOutputStream;

/**
 * Remote call using intermediate buffer
 *
 * @author kari
 */
public final class BufferCall extends ServiceCall {
    public static final int DEFAULT_COMPRESS_THRESHOLD = 500;
    /**
     * block size for socket read: MAX == 8192
     */
    private static final int READ_BLOCK_SIZE = 8192;
    
    /**
     * For decoding call
     */
    public BufferCall() {
        super();
    }
    
    /**
     * @param pSessionId Identifies session for session authentication, 
     * can be null
     * @param pParams null if no params
     */
    public BufferCall(
            Object pSessionId,
            boolean pSessionIdChanged,
            short pServiceUUID, 
            short pMethodId,
            Object[] pParams) 
    {
        super(pSessionId, pSessionIdChanged, pServiceUUID, pMethodId, pParams);
    }

    @Override
    public CallType getType() {
        return CallType.BUFFER_CALL;
    }

    @Override
    protected void write(Handler pHandler, DataOutputStream pOut) 
        throws Exception
    {
        ObjectOutputStream oo = pHandler.createObjectOut();
        writeObjectOut(oo);
        pHandler.finishObjectOut(oo);
        
        writeBuffer(pHandler, pOut);
    }
    
    @Override
    protected void read(Handler pHandler, DataInputStream pIn) 
        throws IOException,
            ClassNotFoundException
    {
        ObjectInputStream oi = readBuffer(pHandler, pIn);
        readObjectIn( oi );
        pHandler.finishObjectIn(oi);
    }

    /**
     * Write contents of {@link Handler#getByteOut()} into pOut
     */
    protected static void writeBuffer(
            final Handler pHandler,
            final DataOutputStream pOut)
            throws IOException 
    {
        final byte[] data;
        final int totalCount;
        {
            final DirectByteArrayOutputStream bout = pHandler.getByteOut();
            data = bout.getBuffer();
            totalCount = bout.size();
        }
        
        final boolean compressed = totalCount > pHandler.getCompressThreshold();
        
        pOut.writeBoolean(compressed);
        
        if (compressed) {
            CallUtil.writeCompactInt(pOut, totalCount);
            
            final Deflater deflater = pHandler.getDeflater();
            final byte[] writeBuffer = pHandler.getDataBuffer();
            
            deflater.setInput(data, 0,  totalCount);
            deflater.finish();
            
            while (!deflater.finished()) {
                int count = deflater.deflate(writeBuffer, 0, writeBuffer.length);
                if (count > 0) {
                    pOut.write(writeBuffer, 0, count);
                }
            }
            deflater.reset();
        } else {
            if (pHandler.isReuseObjectStream()) {
                CallUtil.writeCompactInt(pOut, totalCount);
            }
            pOut.write(data, 0, totalCount);
        }
    }

    /**
     * Read data from pIn to buffer
     */
    protected static ObjectInputStream readBuffer(
            final Handler pHandler,
            final DataInputStream pIn) 
        throws IOException
    {
        ObjectInputStream result;
        
        final boolean compressed = pIn.readBoolean();
        

        if (compressed) {
            final int totalCount = CallUtil.readCompactInt(pIn);
            
            final byte[] data = pHandler.prepareByteOut(totalCount);
            final byte[] readBuffer = pHandler.getDataBuffer();
            final Inflater inflater = pHandler.getInflater();
            
            int offset = 0;
            int remaining = totalCount;
            while (remaining > 0) {
                if (inflater.needsInput()) {
                    final int count = pIn.read(
                            readBuffer, 
                            0, 
                            Math.min(
                                    READ_BLOCK_SIZE, 
                                    Math.min(totalCount, readBuffer.length)));
                    inflater.setInput(readBuffer, 0, count);
                }
                
                try {
                    int writeCount = inflater.inflate(data, offset, Math.min(10, totalCount - offset));
                    offset += writeCount;
                    remaining -= writeCount;
                } catch (DataFormatException e) {
                    throw new IOException(e);
                }
                
            }
            inflater.reset();
            

            result = pHandler.createObjectIn(totalCount);
        } else {
            if (pHandler.isReuseObjectStream()) {
                final int totalCount = CallUtil.readCompactInt(pIn);
                final byte[] data = pHandler.prepareByteOut(totalCount);
                
                int remaining = totalCount;
                int offset = 0;
                while (remaining > 0) {
                    final int count = pIn.read(
                            data, 
                            offset, 
                            Math.min(READ_BLOCK_SIZE, totalCount - offset));
                    if (count > 0) {
                        remaining -= count;
                        offset += count;
                    }
                }
                result = pHandler.createObjectIn(totalCount);
            } else {
                result = pHandler.getIOFactory().createObjectInput(pIn, false);
            }
        }
        
        return result;
    }

    @Override
    protected Result createResult(Object pResult) {
        return new BufferResult(pResult);
    }

}
