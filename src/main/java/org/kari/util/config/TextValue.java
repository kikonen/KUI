package org.kari.util.config;

/**
 * Text value
 *
 * @author kari
 */
public class TextValue extends ConfigValue {
    private final String mDefaultValue;

    public TextValue(String pKey, String pDefaultValue) {
        super(pKey);
        mDefaultValue = pDefaultValue;
    }

    public final String getDefaultValue() {
        return mDefaultValue;
    }

    /**
     * @return {@link #getDefaultValue()} if no value set
     */
    public final String getText() {
        String value = getValue();
        return value != null
            ? value
            : mDefaultValue;
    }
}
