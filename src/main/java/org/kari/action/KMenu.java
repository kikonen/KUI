package org.kari.action;

import java.awt.Component;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;

import org.kari.resources.ResKey;
import org.kari.resources.ResourceAdapter;
import org.kari.resources.WidgetResources;

/**
 * Action based menu
 */
public class KMenu extends KAction {

    public KMenu(final String pActionName, final Action... pItems) {
        super(pActionName);
        putValue(KEY_ITEMS, pItems);
    }

    /**
     * Create context menu for given pComponent
     */
    public ContextActionListener createContextMenu(JComponent pComponent) {
        KMenuImpl m = (KMenuImpl)create(pComponent);
        ContextActionListener result = new ContextActionListener(pComponent, m);
        return result;
    }

    public JMenu create(Component pComponent) {
        return create(pComponent, null);
    }

    /**
     * @param pParent Parent menu into which new menu is attached
     */
    private JMenu create(Component pComponent, final KMenuImpl pParent) {
        final KMenuImpl menu = new KMenuImpl(null, this);
        final ActionMap am = KAction.getActionMap(pComponent);
        
        final Action[] items = (Action[])getValue(KEY_ITEMS);
        if (items!=null) {
            for (int i=0; i<items.length; i++) {
                final Action action = items[i];
                if (action != null) {
                    JMenuItem item = addItem(pComponent, menu, action);
                    if (item != null && action.getValue("tab") != null) {
                        item.setToolTipText("Right click for options");
                    }
                }
            }
        }
        
        return menu;
    }
    
    private JMenuItem addItem(
        final Component pComponent,
        final KMenuImpl pMenu, 
        final Action pAction) 
    {
        JMenuItem item = null;
        final String key = (String)pAction.getValue(Action.NAME);
        
        if (pAction instanceof KMenu) {
            final KMenu actionMenu = (KMenu)pAction;
            item = actionMenu.create(pComponent, pMenu);
        } else if (pAction==KAction.SEPARATOR) {
            pMenu.addSeparator();
        } else {
            ActionGroup group = (ActionGroup)pAction.getValue(KAction.KEY_GROUP);
            if (group != null) {
                item = new JRadioButtonMenuItem(pAction);
                ((JRadioButtonMenuItem)item).setSelected(group.getSelected() == pAction);
                ButtonGroup bg = group.getButtonGroup(ResKey.MENU);
                bg.add(item);
            } else {
                item = new JMenuItem(pAction);
            }
            
            final WidgetResources wr = ResourceAdapter.getInstance().getWidget(key, ResKey.MENU);
            Icon icon = (Icon)pAction.getValue(Action.SMALL_ICON);
            if (icon == null) {
                icon = wr.getIcon();
            }
            
            item.setText(wr.getText());
            item.setIcon(icon);
            item.setMnemonic(wr.getMnenomnic());
            item.setDisplayedMnemonicIndex(wr.getMnenomnicIndex());
            item.setAccelerator(wr.getAccelerator());
        }
        if (item!=null) {
            pMenu.add(item);
        }
        return item;
    }

}
