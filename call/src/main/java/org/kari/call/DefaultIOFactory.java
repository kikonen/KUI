package org.kari.call;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import org.kari.call.io.IOFactory;

/**
 * Default io factory. Default IO factory doesn't currently provide
 * special optimized no-desc IO stream (not packaged within call.jar
 * currently).
 * 
 * <p>NOTE KI Default factory requires JDK 7 due to GZipOutputStream
 * flushing logic.
 * 
 * <p>TODO KI re-package KUI so that compact "no-desc" object streams are
 * part of call.jar
 *
 * @author kari
 */
public final class DefaultIOFactory implements IOFactory {
    public static final IOFactory INSTANCE = new DefaultIOFactory();

    @Override
    public ObjectOutputStream createObjectOutput(OutputStream pOut) 
        throws IOException 
    {
        return new ObjectOutputStream(pOut);
    }

    @Override
    public ObjectInputStream createObjectInput(InputStream pIn) 
        throws IOException 
    {
        return new ObjectInputStream(pIn);
    }
    
}
