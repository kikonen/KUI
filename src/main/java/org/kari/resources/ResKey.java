package org.kari.resources;

/**
 * Resource identifier keys
 */
public interface ResKey {

    String MENU     = "M";
    String TOOLBAR    = "T";
    String BUTTON    = "B";

    /**
     * Mnemonic char: AMBERSAND
     */
    char CHAR_MNEMONIC        = '&';
    
    String TEXT        = "T_";
    String ICON        = "I_";
    String ACC        = "A_";
    String TIP        = "P_";
    
    String T_TEXT   = TOOLBAR + TEXT;
    String T_ICON   = TOOLBAR + ICON;
    String T_TIP    = TOOLBAR + TIP;
}
