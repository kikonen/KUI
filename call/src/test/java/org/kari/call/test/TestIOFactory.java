package org.kari.call.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.kari.call.io.IOFactory;
import org.kari.io.CompactObjectInputStream;
import org.kari.io.CompactObjectOutputStream;


public class TestIOFactory implements IOFactory {
    /**
     * "no desc" out/in object streams preferred
     */
    public static final boolean NO_DESC = true;

    @Override
    public ObjectOutputStream createObjectOutput(OutputStream pOut) 
        throws IOException 
    {
        return NO_DESC
            ? new CompactObjectOutputStream(pOut)
            : new ObjectOutputStream(pOut);
    }

    @Override
    public ObjectInputStream createObjectInput(InputStream pIn) 
        throws IOException 
    {
        return NO_DESC
            ? new CompactObjectInputStream(pIn)
            : new ObjectInputStream(pIn);
    }

}
