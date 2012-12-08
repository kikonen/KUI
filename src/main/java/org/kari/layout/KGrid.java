package org.kari.layout;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JComponent;
import javax.swing.JLabel;

import org.kari.resources.ResUtil;

/**
 * Simple layout builder utility
 */
public class KGrid extends GridBagConstraints {

    public static final int BORDER = 12;

    public static final int SPACE = 5;

    private final JComponent mContainer;

    private final Insets mInsets;

    private final KGridLayout mLayout;

    public KGrid(final JComponent pComponent) {
        this(pComponent, BORDER, SPACE);
    }

    public KGrid(final JComponent pComponent, final int pBorder) {
        this(pComponent, pBorder, SPACE);
    }

    public KGrid(
        final JComponent pContainer,
        final int pBorder,
        final int pSpace)
    {
        mContainer = pContainer;
        mLayout = new KGridLayout(pBorder, pSpace);
        pContainer.setLayout(mLayout);
        mInsets = new Insets(pSpace, pSpace, 0, 0);
//        mContainer.setBorder(BorderFactory.createLineBorder(Color.RED));
        reset();
    }

    public JComponent getComponent() {
        return mContainer;
    }

    public void reset() {
        anchor = NORTHWEST;
        ipadx = 0;
        ipady = 0;
        gridx = 1;
        gridy = 1;
        gridwidth = 1;
        gridheight = 1;
        weightx = 0;
        weighty = 0;
        fill = NONE;
        insets = mInsets;
    }

    public JLabel label(final int pX, final int pY, final String pLabel) {
        final int index = ResUtil.getMnemonicIndex(pLabel);
        final JLabel label = new JLabel(ResUtil.getText(pLabel, index));
        if (index != -1) {
            label.setDisplayedMnemonic(pLabel.charAt(index));
            label.setDisplayedMnemonicIndex(index);
        }
        return (JLabel) add(pX, pY, label);
    }

    public Component fillX(
        final int pX,
        final int pY,
        final JComponent pComponent)
    {
        fill = HORIZONTAL;
        weightx = 1.0;
        return add(pX, pY, pComponent);
    }

    public Component fillY(
        final int pX,
        final int pY,
        final JComponent pComponent)
    {
        fill = VERTICAL;
        weighty = 1.0;
        return add(pX, pY, pComponent);
    }

    public Component fillXY(
        final int pX,
        final int pY)
    {
        fill = BOTH;
        weightx = 1.0;
        weighty = 1.0;
        return add(pX, pY, new Strut(0));
    }

    public Component fillXY(
        final int pX,
        final int pY,
        final Component pComponent)
    {
        fill = BOTH;
        weightx = 1.0;
        weighty = 1.0;
        return add(pX, pY, pComponent);
    }

    public Component add(
        final int pX,
        final int pY,
        final Component pComponent)
    {
        gridx = pX;
        gridy = pY;
//        pComponent.setBorder(BorderFactory.createLineBorder(Color.BLUE));

        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = anchor;
        constraints.ipadx = ipadx;
        constraints.ipady = ipady;
        constraints.gridx = gridx;
        constraints.gridy = gridy;
        constraints.gridwidth = gridwidth;
        constraints.gridheight = gridheight;
        constraints.weightx = weightx;
        constraints.weighty = weighty;
        constraints.fill = fill;
        constraints.insets = insets == mInsets
            ? (Insets) insets.clone()
            : insets;
        mContainer.add(pComponent, constraints);

        reset();
        return pComponent;
    }
}
