package org.kari.util.config;

import java.io.File;
import java.io.IOException;

/**
 * Path value: either dir or file
 *
 * @author kari
 */
public final class PathValue extends TextValue {
    private final PathValue mRelativeTo;

    /**
     * @param pRelativeTo null if not default relative path
     * @param pDefaultValue null if no default value
     */
    public PathValue(
            String pKey,
            PathValue pRelativeTo,
            String pDefaultValue) 
    {
        super(pKey, pDefaultValue);
        mRelativeTo = pRelativeTo;
    }

    /**
     * @return null if no default relative path
     */
    public PathValue getRelativeTo() {
        return mRelativeTo;
    }

    /**
     * <p>pRelativeTo == getRelativeTo()
     * 
     * @return {@link #getDefaultValue()} if no value set
     */
    public File getPath() {
        return getPath(mRelativeTo);
    }
    
    /**
     * @param pRelativeTo if not null path is handled as relative to this
     * @return {@link #getDefaultValue()} if no value set
     */
    public File getPath(PathValue pRelativeTo) {
        return getPath(pRelativeTo != null ? pRelativeTo.getPath() : null);
    }

    /**
     * @param pRelativeTo if not null path is handled as relative to this
     * @return {@link #getDefaultValue()} if no value set
     */
    public File getPath(File pRelativeTo) {
        File path = null;
        String value = getText();

        if (value != null) {
            if (pRelativeTo != null) {
                path = new File(pRelativeTo, value);
            } else {
                path = new File(value);
            }
        }
        
        return path;
    }

    /**
     * @see #exists(File)
     * 
     * <p>pRelativeTo == getRelativeTo()
     */
    public boolean exists() {
        return exists(mRelativeTo);
    }
    
    /**
     * @see #exists(File)
     * 
     * @param pRelativeTo if not null path is handled as relative to this
     */
    public boolean exists(PathValue pRelativeTo) {
        return exists(pRelativeTo != null ? pRelativeTo.getPath() : null);
    }

    /**
     * NPE safe existence check
     * 
     * @param pRelativeTo if not null path is handled as relative to this
     */
    public boolean exists(File pRelativeTo) {
        File path = getPath(pRelativeTo);
        return path != null && path.exists();
    }

    /**
     * @see #createDir(File)
     */
    public boolean createDir() 
        throws IOException
    {
        return exists( (File)null );
    }
    
    /**
     * @see #createDir(File)
     */
    public void createDir(PathValue pRelativeTo) 
        throws IOException
    {
        createDir(pRelativeTo.getPath());
    }

    /**
     * NPE safe create dir create.
     * 
     * @param pRelativeTo if not null path is handled as relative to this
     * 
     * @throws IOException If create is not possible
     */
    public void createDir(File pRelativeTo)
        throws IOException  
    {
        File path = getPath(pRelativeTo);
        if (path == null || !path.mkdirs()) {
            throw new IOException("Failed to create: " + path + " (" + getKey() + ")");
        }
    }

}
