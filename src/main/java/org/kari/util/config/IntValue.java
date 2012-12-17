package org.kari.util.config;

/**
 * Text value
 *
 * @author kari
 */
public final class IntValue extends ConfigValue {
    private int mDefaultValue;

    public IntValue(String pKey, int pDefaultValue) {
        super(pKey);
        mDefaultValue = pDefaultValue;
    }

    public int getDefaultValue() {
        return mDefaultValue;
    }

    /**
     * @return {@link #getDefaultValue()} if no value set
     */
    public int getInt() {
        String value = getValue();
        return value != null
            ? Integer.parseInt(value)
            : mDefaultValue;
    }
}
    