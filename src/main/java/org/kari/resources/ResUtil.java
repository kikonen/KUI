package org.kari.resources;

import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Utilities for resources
 */
public abstract class ResUtil {

    /**
     * Utility: No instances
     */
    private ResUtil() {
    }
    
    /**
     * Get mnemonic index
     * 
     * TODO KI Escape char handling for ambersand
     * 
     * @param pText
     * @return Index of mnemonic, -1 if there is no mnemonic
     */
    public static final int getMnemonicIndex(final String pText) {
        int index = pText.indexOf(ResKey.CHAR_MNEMONIC);
//        if (index<pText.length()-2 && pText.charAt(index)==ResKey.CHAR_MNEMONIC) {
//            index = -1;
//        }
        return index;
    }
    
    /**
     * Split mnemonic part away from the string
     * @param pText
     * @return
     */
    public static final String getText(final String pText, final int pMnemonicIndex) {
        String result = pText;
        if (pMnemonicIndex!=-1) {
            final int len = pText.length();
            final StringBuffer sb = new StringBuffer(len);
            if (pMnemonicIndex>0)
                sb.append(pText.substring(0, pMnemonicIndex));
            if (pMnemonicIndex<len-1)
                sb.append(pText.substring(pMnemonicIndex+1, len));
            result = sb.toString();
        }
        return result;
    }

    public static final Icon getIcon(String pName) {
        return getIcon(pName, null);
    }

    public static final Icon getIcon(String pName, Icon pDefault) {
        URL url = ResUtil.class.getResource(pName);
        return url != null
            ? new ImageIcon(url)
            : pDefault;
    }
}
