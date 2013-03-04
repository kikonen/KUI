package org.kari.perspective;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * Message shown in glasspane
 */
public class KGlassMessage extends JComponent 
    implements 
        ActionListener
{

    private final JLabel mTitle = new JLabel("Post IT");
    private final JLabel mMessage = new JLabel();
    
    private Timer mTimer;
    
    private float mAlpha = 1.0f;
    
    private static int mIndex;
    
    private final MouseListener mMouseListener = new MouseAdapter() {
        @Override
        public void mouseClicked(MouseEvent e) {
            kill();
        }
    };

    public KGlassMessage() {
        setLayout(new BorderLayout());
        setOpaque(false);
        
        add(mTitle, BorderLayout.NORTH);
        mTitle.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        add(mMessage, BorderLayout.CENTER);
        setBorder(BorderFactory.createLineBorder(Color.BLACK));
        addMouseListener(mMouseListener);
    }

    
    @Override
    public void actionPerformed(ActionEvent e) {
        mAlpha -= 0.025;
        if (mAlpha<0.1) {
            kill();
        }
        repaint();
        if (getParent()!=null && mTimer!=null) {
            mTimer.restart();
        }
    }

    public void showMessage(
        final Component pOwner,
        final String pMessage,
        final Point pLoc,
        final Dimension pSize)
    {
        mMessage.setText(pMessage);
        JFrame frame = (JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, pOwner);
        JComponent pane = frame.getLayeredPane();
        Point loc = SwingUtilities.convertPoint(pOwner, pLoc.x, pLoc.y, pane);
        setSize(pSize);
        setLocation(loc);
        pane.add(this, new Integer(1000), mIndex++);
        setVisible(true);
        revalidate();
//        pane.setVisible(true);
//        pane.repaint();
    }
    
    @Override
    public void setVisible(boolean pVisible)
    {
        if (pVisible) {
            if (mTimer==null) {
                mTimer = new Timer(250, this);
            }
            mTimer.start();
        } else {
            if (mTimer!=null) {
                mTimer.stop();
                mTimer = null;
            }
        }
        super.setVisible(pVisible);
    }


    @Override
    public void paint(Graphics g)
    {
        final Graphics2D g2d = (Graphics2D)g;
        final Composite originalComposite = g2d.getComposite();
        final Color color = Color.YELLOW;//UIManager.getColor("ToolTip.backgroundInactive");
        g2d.setColor(color);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, mAlpha));
        g2d.setPaint(color);
        Rectangle rect = new Rectangle(0, 0, getWidth(), getHeight());
        g2d.fill(rect);
//        g2d.setComposite(originalComposite);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, mAlpha));
        super.paint(g);
    }


    private void kill() {
        setVisible(false);
        getParent().remove(KGlassMessage.this);
        mTimer = null;
    }
    
}
