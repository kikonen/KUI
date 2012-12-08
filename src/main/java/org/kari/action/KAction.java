package org.kari.action;

import java.awt.Component;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.KeyStroke;

import org.apache.log4j.Logger;
import org.kari.resources.ResKey;
import org.kari.resources.ResourceAdapter;
import org.kari.resources.WidgetResources;

/**
 * Base class of K-Actions
 */
public class KAction extends AbstractAction {

    public static final Logger LOG = Logger.getLogger("ki.action");

    public static final String KEY_ITEMS = "items";
    public static final String KEY_CHECK = "check";
    public static final String KEY_GROUP = "group";

    public static final KAction SEPARATOR = new KAction("SEPARATOR");

    /**
     * @param pActionName Id identifying action and it's resources
     */
    public KAction(String pActionName) 
    {
        this(pActionName, null);
    }
    
    /**
     * @param pActionName Id identifying action and it's resources
     * @param pGroup for radiobuttons
     */
    public KAction(final String pActionName, ActionGroup pGroup) {
        super(pActionName);
        if (pGroup != null) {
            putValue(KEY_GROUP, pGroup);
            pGroup.addAction(this);
            setSelected(false);
        }
    }
    
    /**
     * final, to allow framework to perform delegation of action invoking
     */
    public final void actionPerformed(final ActionEvent pEvent) {
        actionPerformed(new ActionContext(pEvent));
//        ActionGroup group = (ActionGroup)getValue(KAction.KEY_GROUP);
//        if (group != null) {
//            if 
//            Boolean selected = (Boolean)getValue(SELECTED_KEY);
//            if (selected != null && selected.booleanValue()) {
//                group.setSelected(this);
//            }
//        }
    }

    public void actionPerformed(final ActionContext pCtx) {
        WidgetResources wr = ResourceAdapter.getInstance().getWidget(getName(), ResKey.MENU);
        LOG.info("Action invoked:" + wr.getText());
    }

    public String getName() {
        return (String) getValue(NAME);
    }
    
    public boolean isSelected() {
        Boolean selected = (Boolean)getValue(SELECTED_KEY);
        return selected != null && selected.booleanValue();
    }
    
    public void setSelected(boolean pSelected) {
        putValue(SELECTED_KEY, pSelected ? Boolean.TRUE : Boolean.FALSE);
    }

    /**
     * @return Action map of component, for window, root pane is used
     * as holder of action map
     */
    public static ActionMap getActionMap(Component pComponent) {
        return pComponent instanceof JComponent
            ? ((JComponent)pComponent).getActionMap()
            : ((JFrame)pComponent).getRootPane().getActionMap();
    }
    
    /**
     * Bind keyboard accelerators (if any) into pComponent 
     */
    public void bind(JComponent pComponent) {
        bind(pComponent, JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }
    
    /**
     * Bind keyboard accelerators (if any) into pComponent 
     */
    public void bind(JComponent pComponent, int pInputType) {
        String actionName = getName();
        WidgetResources wr = ResourceAdapter.getInstance().getWidget(actionName, ResKey.MENU);
        KeyStroke acc = wr.getAccelerator();
        if (acc != null) {
            pComponent.getInputMap(pInputType).put(acc, actionName);
            pComponent.getActionMap().put(actionName, this);
        }
    }

}
