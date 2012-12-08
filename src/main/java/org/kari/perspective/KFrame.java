package org.kari.perspective;

import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.log4j.Logger;
import org.kari.resources.ResKey;
import org.kari.resources.ResourceAdapter;
import org.kari.resources.WidgetResources;

/**
 * Base frame for KUI
 */
public class KFrame extends JFrame {

    public static final Logger LOG = Logger.getLogger("ki.frame");
    
    private final WindowListener mWindowListener = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent pE) {
            dispose();
        }

        @Override
        public void windowClosed(WindowEvent pE) {
            // Nothing
        }
    };

    
    public KFrame() {
        setGlassPane(new KGlassPane());
        setContentPane(createContentPanel());
        setTitle("KUI Frame");
        setSize(new Dimension(400, 300));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(mWindowListener);
    }

    protected JComponent createContentPanel() {
        return new JPanel();
    }

    /**
     * Set frame icon based into {@link WidgetResources}
     */
    public void setIcon(String pResourceKey) {
        WidgetResources wr = ResourceAdapter.getInstance().getWidget(
                pResourceKey, 
                ResKey.MENU);
        setIcon(wr.getIcon());
    }

    public void setIcon(Icon pIcon) {
        if (pIcon instanceof ImageIcon) {
            setIconImage( ((ImageIcon)pIcon).getImage() );
        }
    }
}
