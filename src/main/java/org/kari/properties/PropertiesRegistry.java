package org.kari.properties;

import java.util.HashMap;
import java.util.Map;

/**
 * Central registry for properties dialogs
 * 
 * @author kari
 */
public final class PropertiesRegistry {
    private static PropertiesRegistry mInstance;
    
    private final Map<Class<?>, Class<? extends KPropertiesFrame>> mBindings =
        new HashMap<Class<?>, Class<? extends KPropertiesFrame>>();

    private PropertiesRegistry() {
        super();
    }

    public static PropertiesRegistry getInstance() {
        if (mInstance == null) {
            mInstance = new PropertiesRegistry();
        }
        return mInstance;
    }

    public void register(Class<?> pType, Class<? extends KPropertiesFrame> pDialogType) {
        mBindings.put(pType, pDialogType);
    }

    /**
     * @return Dialog for type, null if nones
     */
    public Class<? extends KPropertiesFrame> getBinding(Class<?> pType) {
        return mBindings.get(pType);
    }
}
