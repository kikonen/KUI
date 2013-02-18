package org.kari.call.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.kari.call.io.IOFactory;
import org.kari.io.CompactObjectInputStream;
import org.kari.io.CompactObjectOutputStream;


public class TestIOFactory implements IOFactory {
    /**
     * "no desc" out/in object streams preferred
     */
    public static final boolean NO_DESC = true;

    @Override
    public ObjectOutputStream createObjectOutput(
            OutputStream pOut,
            boolean pCompressed) 
        throws IOException 
    {
        OutputStream out = pCompressed
            ? new GZIPOutputStream(pOut, true) 
            : pOut;
        return NO_DESC
            ? new CompactObjectOutputStream(out)
            : new ObjectOutputStream(out);
    }

    @Override
    public ObjectInputStream createObjectInput(
            InputStream pIn,
            boolean pCompressed) 
        throws IOException 
    {
        InputStream in = pCompressed
            ? new GZIPInputStream(pIn)
            : pIn;
        return NO_DESC
            ? new CompactObjectInputStream(in)
            : new ObjectInputStream(in);
    }

}
