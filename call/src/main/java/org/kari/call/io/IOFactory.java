package org.kari.call.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Abstraction for IO streams
 *
 * @author kari
 */
public interface IOFactory {
    ObjectOutputStream createObjectOutput(
            OutputStream pOut, 
            boolean pCompressed)
        throws IOException;
    
    ObjectInputStream createObjectInput(
            InputStream pIn, 
            boolean pCompressed)
        throws IOException; 
}
