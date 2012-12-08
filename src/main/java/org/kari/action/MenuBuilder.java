package org.kari.action;

import javax.swing.JMenu;

/**
 * Menu builder constructing menus
 */
public abstract class MenuBuilder {

    /**
     * Utility: no instances allowed
     */
    private MenuBuilder() {
        // Nothing
    }
    
    public JMenu get(final KMenu pMenu) {
        return get(null, pMenu);
    }
    
    public JMenu get(final KMenuImpl pParent, final KMenu pMenu) {
        final KMenuImpl menu = new KMenuImpl(null, pMenu);
        return menu;
    }

}
