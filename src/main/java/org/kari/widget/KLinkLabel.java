package org.kari.widget;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.JLabel;

public class KLinkLabel extends JLabel {

    Action mAction;
    
    private final MouseListener mMouseListener = new MouseAdapter() {

        @Override
        public void mouseEntered(MouseEvent pE) {
            if (isEnabled()) {
                setForeground(Color.BLUE);
//                setBackground(new Color(153,153, 204));
            }
        }

        @Override
        public void mouseExited(MouseEvent pE) {
            if (isEnabled()) {
                setForeground(Color.BLACK);
//                setBackground(UIManager.getColor("control"));
            }
        }

        @Override
        public void mouseReleased(MouseEvent pE) {
            if (mAction != null) {
                if (pE.getButton() == MouseEvent.BUTTON1) {
                    mAction.actionPerformed(null);
                }
            }
        }
    };

    public KLinkLabel() {
        this(null);
    }

    public KLinkLabel(String pText) {
        this(pText, null);
    }
    
    public KLinkLabel(String pText, Action pAction) {
        super(pText);
//        setOpaque(true);
        addMouseListener(mMouseListener);
        setAction(pAction);
    }

    public Action getAction() {
        return mAction;
    }

    public void setAction(Action pAction) {
        mAction = pAction;
        if (mAction != null) {
            setEnabled(true);
        }
    }
    
}
