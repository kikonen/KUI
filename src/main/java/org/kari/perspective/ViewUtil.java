package org.kari.perspective;

import java.awt.Component;
import java.awt.Rectangle;
import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Utilities for view handling
 *
 * @author kari
 */
public final class ViewUtil {
    /**
     * Find frame containing pComp
     * @return Frame, null if none or dialog
     */
    public static JFrame getFrame(Component pComp) {
        Window window = SwingUtilities.getWindowAncestor(pComp);
        return window instanceof JFrame
            ? (JFrame)window
            : null;
    }

    /**
     * Get center bounds for pWindow
     */
    public static Rectangle getCenterBounds(Window pWindow) {
        int x = 0;
        int y = 0;
        int width = pWindow.getWidth();
        int height = pWindow.getHeight();
        
        Window owner = pWindow.getOwner();
        if (owner != null) {
            Rectangle ownerBounds = owner.getBounds();
            x = ownerBounds.x + (ownerBounds.width - width)/2;
            y = ownerBounds.y + (ownerBounds.height - height)/2;
        }
        
        return new Rectangle(x, y, width, height);
    }
}
