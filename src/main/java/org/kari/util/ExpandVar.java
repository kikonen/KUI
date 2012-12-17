package org.kari.util;

import java.util.Properties;


/**
 * Variables are referenced using "${var}" syntax
 * 
 * <p>Variables are resolved in this order: 
 * <ul>
 * <li>local-env
 * <li>system properties
 * <li>environment variables
 * </ul>
 * 
 * @author kari
 *
 */
public final class ExpandVar {
    private final Properties mEnv;

    /**
     * <p>pEnv == null
     */
    public ExpandVar() {
        this(null);
    }

    /**
     * @param pEnv null for no env
     */
    public ExpandVar(Properties pEnv) {
        mEnv = pEnv;
    }

    private String getValue(String pKey) {
        String result = null;
        if (mEnv != null) {
            result = mEnv.getProperty(pKey);
        }
        if (result == null) {
            result = System.getProperty(pKey);
        }
        if (result == null) {
            result = System.getenv(pKey);
        }
        return result;
    }
    
    /**
     * Expand all variables in pText
     * 
     * @param pRecursive if true then variable expanding into variable is
     * recursively expanded
     * 
     * @return pText with all variables expanded, if null, nothing is done
     */
    public String expand(
            String pText, 
            boolean pRecursive) 
    {
        String result = pText;
        
        int startIdx = pText != null ? pText.indexOf("${") : -1;
        if (startIdx != -1) {
            StringBuilder sb = new StringBuilder();
            int lastEndIdx = -1;
            
            do {
                int endIdx = pText.indexOf("}", startIdx + 2);
                if (endIdx != -1) {
                    String key = pText.substring(startIdx + 2, endIdx);
                    
                    String value = getValue(key);
                    if (pRecursive) {
                        // TODO KI stop infinite recursion?!?
                        value = expand(value, true);
                    }
                    
                    sb.append(pText.substring(lastEndIdx + 1, startIdx));
                    sb.append(value);
                    
                    lastEndIdx = endIdx;
                    startIdx = pText.indexOf("${", endIdx + 1);
                } else {
                    startIdx = -1;
                }
            } while (startIdx != -1);
            
            if (lastEndIdx < pText.length()) {
                sb.append(pText.substring(lastEndIdx + 1, pText.length()));
            }
            
            result = sb.toString();
        } 
        
        return result;
    }
    
}
