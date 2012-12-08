package org.kari.action;

import javax.swing.JComponent;


/**
 * Action which works as placeholder for arbitary component
 * to be embedded into layout (ex. toolbar)
 * 
 * @author kari
 */
public final class KComponentAction extends KAction {
    private JComponent mComponent;
    
    /**
     * @param pComponent Component to be attached
     */
    public KComponentAction(JComponent pComponent) {
        super("component");
        mComponent = pComponent;
    }

    public JComponent getComponent() {
        return mComponent;
    }

}
