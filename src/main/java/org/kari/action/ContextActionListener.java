package org.kari.action;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.JTable;

/**
 * Simple context menu listener
 * 
 * @author kari
 */
public final class ContextActionListener 
    implements 
        MouseListener,
        KeyListener
{
    private static final String KEY_CML = "kui_cml";
    
    private final KMenuImpl mMenu;
    private JComponent mComponent;
    
    public ContextActionListener(JComponent pComponent, KMenuImpl pMenu) {
        mComponent = pComponent;
        mMenu = pMenu;
    }
    
    public void start() {
        ContextActionListener old = (ContextActionListener)mComponent.getClientProperty(KEY_CML);
        if (old != null) {
            mComponent.removeMouseListener(old);
            mComponent.removeKeyListener(old);
        }
        mComponent.addMouseListener(this);
        mComponent.addKeyListener(this);
        mComponent.putClientProperty(KEY_CML, this);
    }
    
    public void stop() {
        mComponent.removeMouseListener(this);
    }

    @Override
    public void mouseClicked(MouseEvent pE) {
        // Ignore
    }

    @Override
    public void mouseEntered(MouseEvent pE) {
        // Ignore
    }

    @Override
    public void mouseExited(MouseEvent pE) {
        // Ignore
    }

    @Override
    public void mousePressed(MouseEvent pEvent) {
        if (pEvent.isPopupTrigger()) {
            showMenu(pEvent);
        }
    }

    @Override
    public void mouseReleased(MouseEvent pEvent) {
        if (pEvent.isPopupTrigger()) {
            showMenu(pEvent);
        }
    }
    
    @Override
    public void keyPressed(KeyEvent pEvent) {
        // Ignore
        if (isContextMenuKey(pEvent)) {
            MouseEvent me = new MouseEvent(
                    pEvent.getComponent(),
                    pEvent.getID(),
                    pEvent.getWhen(),
                    pEvent.getModifiers(),
                    -1,
                    -1,
                    1,
                    true,
                    MouseEvent.BUTTON2);
            showMenu(me);
        }
    }

    private boolean isContextMenuKey(KeyEvent pEvent) {
        int keyCode = pEvent.getKeyCode();
        return keyCode == KeyEvent.VK_CONTEXT_MENU
            || keyCode == KeyEvent.VK_F10 && pEvent.isShiftDown();
    }

    @Override
    public void keyReleased(KeyEvent pEvent) {
        // Ignore
    }

    @Override
    public void keyTyped(KeyEvent pEvent) {
        // Ignore
    }

    public void showMenu(MouseEvent pEvent) {
        JPopupMenu popup = mMenu.getPopupMenu();
        Point point = pEvent.getPoint();
        setSelection(point);
        popup.show(mComponent, point.x, point.y);
    }
    
    private void setSelection(Point pPoint) {
        if (mComponent instanceof JTable) {
            JTable table = (JTable)mComponent;
            int row = table.rowAtPoint(pPoint);
            if (row != -1) {
                int column = table.columnAtPoint(pPoint);
                if (column != -1) {
                    table.clearSelection();
                    table.addRowSelectionInterval(row, row);
                    table.addColumnSelectionInterval(column, column);
                }
            }
        }
    }

}
