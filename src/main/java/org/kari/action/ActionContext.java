package org.kari.action;

import java.awt.Component;
import java.awt.Window;
import java.util.EventObject;

import javax.swing.JPopupMenu;

/**
 * Action invoking context
 * 
 * @author kari
 */
public final class ActionContext {

    private final EventObject mEvent;
    
    public ActionContext(final EventObject pEvent) {
        mEvent = pEvent;
    }

    public EventObject getEvent() {
        return mEvent;
    }

    @Override
    public String toString()
    {
        return "Ctx: " + mEvent.toString();
    }
    
    public Component getComponent() {
        return mEvent.getSource() instanceof Component
            ? (Component)mEvent.getSource()
            : null;
    }
    
    public Window getWindow() {
        return findWindow(getComponent());
    }
    

    /**
     * Find window for component.
     * 
     * <p>NOTE KI Menu items have slightly special inheritance hierarchy, therefore
     * SwingUtilities.getAncestorOfClass() does not work for them directly
     */
    public static Window findWindow(final Component pComp) {
        Component comp = pComp;
        while (comp!=null && !(comp instanceof Window)) {
            if (comp instanceof JPopupMenu) {
                comp = ((JPopupMenu)comp).getInvoker();
            } else {
                comp = comp.getParent();
            }
        }
        return comp instanceof Window
            ? (Window)comp
            : null;
    }


}
