package org.kari.action;

import javax.swing.Action;
import javax.swing.JMenu;

import org.kari.resources.ResKey;
import org.kari.resources.ResourceAdapter;
import org.kari.resources.WidgetResources;


/**
 * Implementation of action menu
 */
public class KMenuImpl extends JMenu {

    private final KMenu mActionMenu;
    private KMenuImpl mParentMenu;
    
    /**
     * @param pParent Parent menu, null for toplevel menu
     * @param pActionMenu Action menu describing menu
     */
    public KMenuImpl(final KMenuImpl pParent, final KMenu pActionMenu) {
        mParentMenu = pParent;
        mActionMenu = pActionMenu;
        initialize();
    }

    private void initialize() {
        final String key = mActionMenu.getName();
        final WidgetResources wr = ResourceAdapter.getInstance().getWidget(key, ResKey.MENU);
        setText(wr.getText());
        setMnemonic(wr.getMnenomnic());
        setDisplayedMnemonicIndex(wr.getMnenomnicIndex());
    }
    
    /**
     * Get parent of menu
     */
    public KMenuImpl getParentMenu() {
        return mParentMenu;
    }

    public KMenu getActionMenu() {
        return mActionMenu;
    }
    
}
