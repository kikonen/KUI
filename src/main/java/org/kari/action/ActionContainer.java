package org.kari.action;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * Wrapper for {@link javax.swing.ActionMap} managing conveniency logic for
 * menu/toolbar actions
 * 
 * @author kari
 */
public final class ActionContainer {
    private final Component mOwner;
    private List<String> mMenus = new ArrayList<String>();
    private List<String> mToolbars = new ArrayList<String>();

    public ActionContainer(JFrame pWindow) {
        mOwner = pWindow;
    }
    
    public ActionContainer(JComponent pOwner) {
        mOwner = pOwner;
    }

    /**
     * @return Action map of component, for window, root pane is used
     * as holder of action map
     */
    private ActionMap getActionMap() {
        return KAction.getActionMap(mOwner);
    }

    public Action getAction(String pActionName) {
        return getActionMap().get(pActionName);
    }

    public KMenu getMenu(String pActionName) {
        return (KMenu)getActionMap().get(pActionName);
    }

    public KToolbar getToolbar(String pActionName) {
        return (KToolbar)getActionMap().get(pActionName);
    }

    /**
     * @return Ordered list of menubar menus
     */
    public List<String> getMenus() {
        return mMenus;
    }

    /**
     * @return Ordered list of toolbars
     */
    public List<String> getToolbars() {
        return mToolbars;
    }

    /**
     * Set menus
     * 
     * @param pMenus menus, null to clear list
     */
    public void setMenus(List<String> pMenus) {
        mMenus = new ArrayList<String>();
        if (pMenus != null) {
            mMenus.addAll(pMenus);
        }
    }

    /**
     * Set toolbars
     * 
     * @param pToolBars toolbars, null to clear list
     */
    public void setToolbars(List<String> pToolbars) {
        mToolbars = new ArrayList<String>();
        if (pToolbars != null) {
            mToolbars.addAll(pToolbars);
        }
    }

    public void add(Action pAction) {
        String name = (String)pAction.getValue(Action.NAME);
        getActionMap().put(name, pAction);
    }

    public void remove(String pActionName) {
        getActionMap().remove(pActionName);
        mMenus.remove(pActionName);
        mToolbars.remove(pActionName);
    }

    /**
     * Add new menu
     */
    public void addMenu(KMenu pMenu) {
        add(pMenu);
        String name = pMenu.getName();
        if (!mMenus.contains(name)) {
            mMenus.add(name);
        }
    }
    
    /**
     * Add new toolbar
     */
    public void addToolbar(KToolbar pToolbar) {
        add(pToolbar);
        String name = pToolbar.getName();
        if (!mToolbars.contains(name)) {
            mToolbars.add(name);
        }
    }

}
