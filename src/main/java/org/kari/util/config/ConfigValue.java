package org.kari.util.config;

/**
 * Base class of config vars
 *
 * @author kari
 */
public abstract class ConfigValue {
    private final String mKey;
    
    public ConfigValue(String pKey) {
        mKey = pKey;
    }

    public final String getKey() {
        return mKey;
    }

    /**
     * @return value from config file, null if not found
     */
    protected final String getValue() {
        return Config.getInstance().getValue(mKey);
    }
}
