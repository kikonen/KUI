package org.kari.perspective;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JMenuBar;
import javax.swing.JPanel;

import org.kari.action.ActionConstants;
import org.kari.action.ActionContainer;
import org.kari.action.ComponentWithActionContainer;
import org.kari.action.KAction;
import org.kari.action.KMenu;
import org.kari.action.KToolbar;
import org.kari.action.KToolbarPanel;

/**
 * Application frame with menus, toolbars and statusbar. Based into
 * perspectives.
 */
public class KApplicationFrame extends KFrame 
    implements ComponentWithActionContainer
{
    private final ActionContainer mActionContainer = new ActionContainer(this);
    private KToolbarPanel mToolbarPanel;
    private boolean mPostInitialized;
    
    public KApplicationFrame() {
        KAction action = new KAction(ActionConstants.R_OPEN);
        KMenu menu = new KMenu(
            ActionConstants.R_MENU_FILE,
            action);
        getActionContainer().addMenu(menu);

        KToolbar toolbar = new KToolbar(
            ActionConstants.R_TB_MAIN,
            action);
        getActionContainer().addToolbar(toolbar);
    }

    @Override
    public void setVisible(boolean pVisible) {
        if (!mPostInitialized) {
            mPostInitialized = true;
            postInitialize();
        }
        super.setVisible(pVisible);
    }
    
    protected void postInitialize() {
        createMenus();
        createToolbars();
    }
    
    public ActionContainer getActionContainer() {
        return mActionContainer;
    }

    protected void createMenus() {
        ActionContainer ac = getActionContainer();
        List<String> menus = ac.getMenus();
        if (!menus.isEmpty()) {
            setJMenuBar(new JMenuBar());
            for (String menuName : menus) {
                KMenu menu = ac.getMenu(menuName);
                getJMenuBar().add(menu.create(this));
            }
        }
    }

    protected void createToolbars() {
        ActionContainer ac = getActionContainer();
        List<String> toolbars = ac.getToolbars();
        if (!toolbars.isEmpty()) {
            KToolbarPanel toolbarPanel = getToolbarPanel();
            for (String ToolbarName : toolbars) {
                KToolbar toolbar = ac.getToolbar(ToolbarName);
                toolbarPanel.add(toolbar.create(this));
            }
        }
    }
    
    public KToolbarPanel getToolbarPanel() {
        return mToolbarPanel;
    }
    

    @Override
    protected final JComponent createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        mToolbarPanel = createToolbarPanel();
        JComponent centerPanel = createCenterPanel();
        
        panel.add(mToolbarPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        
        return panel;
    }

    protected KToolbarPanel createToolbarPanel() {
        return new KToolbarPanel();
    }

    protected JComponent createCenterPanel() {
        return new JPanel();
    }

}
