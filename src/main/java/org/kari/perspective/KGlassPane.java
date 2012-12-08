package org.kari.perspective;

import java.awt.Graphics;

import javax.swing.JComponent;

/**
 * Glass pane used by {@link KFrame}.
 */
public class KGlassPane extends JComponent {

    public KGlassPane() {
        setOpaque(false);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

}
