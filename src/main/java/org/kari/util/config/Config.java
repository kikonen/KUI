package org.kari.util.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.kari.util.ExpandVar;

public class Config {
    private static Config mInstance;
    
    private final String mConfigFile;
    private Properties mProperties;
    
    /**
     * @param pConfigFile Absolute resource path to pConfigFile. Must start with
     * "/".
     */
    public static void init(String pConfigFile) {
        mInstance = new Config(pConfigFile);
    }
    
    public static Config getInstance() {
        return mInstance;
    }

    private Config(String pConfigFile) {
        mConfigFile = pConfigFile;
    }

    private void load() throws IOException {
        Properties properties = new Properties();

        InputStream input = findConfig();
        try {
            properties.load(input);
        } finally {
            if (input != null) {
                input.close();
            }
        }
        mProperties = properties;
    }

    private InputStream findConfig() 
        throws IOException
    {
        InputStream input = null;
        {
            String localPath = new ExpandVar().expand("${user.home}" + mConfigFile, true);
            File file = new File(localPath);
            if (file.exists()) {
                input = new FileInputStream(file);
            }
        }
        if (input == null) {
            input = getClass().getResourceAsStream(mConfigFile);
        }
        return input;
    }
    
    public Properties getProperties() {
        if (mProperties == null) {
            try {
                load();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return mProperties;
    }
    
    /**
     * @return value for pKey, null if not found
     */
    public String getValue(String pKey) {
        return getProperties().getProperty(pKey);
    }
}
