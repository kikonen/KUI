package org.kari.action;

import javax.swing.JToolBar;
import javax.swing.border.EmptyBorder;

/**
 * Implementation of toolbar
 * 
 * @author kari
 */
public class KToolbarImpl extends JToolBar {
    private final KToolbar mToolbar;
    
    public KToolbarImpl(final KToolbar pTooolbar) {
        mToolbar = pTooolbar;
        initialize();
        setFloatable(false);
        setRollover(true);
        setBorder(new EmptyBorder(0, 0, 0, 0));
    }

    private void initialize() {
//        final String key = mToolbar.getName();
    }
    
}
