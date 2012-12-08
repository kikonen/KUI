package org.kari.action;

import java.awt.Component;
import java.awt.Insets;

import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import org.kari.resources.ResKey;
import org.kari.resources.ResUtil;
import org.kari.resources.ResourceAdapter;
import org.kari.resources.WidgetResources;

/**
 * Definition of toolbar
 * 
 * @author kari
 */
public class KToolbar extends KAction {
    private static final Insets ZERO_INSETS = new Insets(0, 0, 0, 0);
    public static final Icon DEF_ICON = ResUtil.getIcon("/icon/sun/Bean24.gif");
    
    public KToolbar(final String pActionName, final Action... pItems) {
        super(pActionName);
        putValue(KEY_ITEMS, pItems);
    }
    
    public JToolBar create(Component pComponent) {
        final KToolbarImpl tb = new KToolbarImpl(this);
        final ActionMap am = KAction.getActionMap(pComponent);
        
        final Action[] items = (Action[])getValue(KEY_ITEMS);
        if (items!=null) {
            for (int i=0; i<items.length; i++) {
                final Action a = items[i];
                JComponent item = addItem(pComponent, tb, a);
                if (item != null && a.getValue("tab") != null) {
                    item.setToolTipText("Right click for options");
                }
            }
        }
        
        return tb;
    }
    
    private JComponent addItem(
        final Component pComponent,
        final KToolbarImpl pToolbar, 
        final Action pAction) 
    {
        JComponent item = null;
        final String key = (String)pAction.getValue(Action.NAME);
        
        if (pAction instanceof KMenu) {
            final KMenu actionMenu = (KMenu)pAction;
            item = actionMenu.create(pComponent);
            pToolbar.add(item);
        } else if (pAction==KAction.SEPARATOR) {
            pToolbar.addSeparator();
        } else if (pAction instanceof KComponentAction) {
            item = ((KComponentAction)pAction).getComponent();
            pToolbar.add(item);
        } else {
            AbstractButton button;
            ActionGroup group = (ActionGroup)pAction.getValue(KAction.KEY_GROUP);
            if (group != null) {
                button = new JToggleButton(pAction);
                ((JToggleButton)button).setSelected(group.getSelected() == pAction);
                ButtonGroup bg = group.getButtonGroup(ResKey.TOOLBAR);
                bg.add(button);
            } else {
                button = new JButton(pAction);
            }
            item = button;
            
            final WidgetResources wr = ResourceAdapter.getInstance().getWidget(key, ResKey.TOOLBAR);
            Icon icon = wr.getIcon();
            if (icon == null) {
                icon = DEF_ICON;
            }
            button.setIcon(icon);
            button.setToolTipText(wr.getToolTip());
            button.setMargin(ZERO_INSETS);
            button.setRequestFocusEnabled(false);
            button.setFocusable(false);
            
            if (false) {
                button.setText(wr.getText());
                button.setMnemonic(wr.getMnenomnic());
                button.setDisplayedMnemonicIndex(wr.getMnenomnicIndex());
            } else {
                button.setText(null);
            }
        }
        
        if (item != null) {
            pToolbar.add(item);
        }
        return item;
    }

}
