package org.kari.properties;

import java.awt.Rectangle;

import javax.swing.JFrame;

import org.kari.perspective.ViewUtil;

/**
 * View properties for given element. Properties dialog is
 * created using registry
 * 
 * @author kari
 */
public final class PropertiesViewer {
    private final JFrame mOwner;
    private final Object mElement;
    private Apply mApply;

    public PropertiesViewer(
        JFrame pOwner,
        Object pElement, 
        Apply pApply) 
    {
        mOwner= pOwner;
        mElement = pElement;
        mApply = pApply;
    }

    public void show() 
        throws Exception
    {
        Class<? extends KPropertiesFrame> dialogType = PropertiesRegistry.getInstance().getBinding(mElement.getClass());
        KPropertiesFrame dialog = dialogType.newInstance();
        dialog.setOwner(mOwner);
        dialog.setApply(mApply);
        dialog.setContent(mElement);
        
        Rectangle bounds = ViewUtil.getCenterBounds(dialog);
        dialog.setBounds(bounds);
        
        dialog.setVisible(true);
    }
}
