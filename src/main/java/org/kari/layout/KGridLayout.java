package org.kari.layout;

import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.Iterator;

/**
 * Extended GridBagLayout
 */
public final class KGridLayout extends GridBagLayout {

    final int mSpace;

    final int mBorder;

    private boolean mLayoutDone;

    public KGridLayout(final int pBorder, final int pSpace) {
        mSpace = pSpace;
        mBorder = pBorder;
    }

    @Override
    public void layoutContainer(Container pContainer) {
        if (!mLayoutDone) {
            final int topBalance = mBorder - mSpace;
            final int bottomBalance = mBorder;

            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxX = 0;
            int maxY = 0;

            final Collection allConstraints = comptable.values();

            for (Iterator constIter = allConstraints.iterator(); constIter
                .hasNext();)
            {
                final GridBagConstraints constraints = (GridBagConstraints) constIter
                    .next();

                if (constraints.gridx < minX) {
                    minX = constraints.gridx;
                } else if (constraints.gridx + (constraints.gridwidth - 1) > maxX)
                {
                    maxX = constraints.gridx;
                }
                if (constraints.gridy < minY) {
                    minY = constraints.gridy;
                } else if (constraints.gridy + (constraints.gridheight - 1) > maxY)
                {
                    maxY = constraints.gridy;
                }
            }

            for (Iterator constIter = allConstraints.iterator(); constIter
                .hasNext();)
            {
                final GridBagConstraints constraints = (GridBagConstraints) constIter
                    .next();
                Insets insets = constraints.insets;

                if (constraints.gridx == minX) {
                    insets.left += topBalance;
                }
                if (constraints.gridx + (constraints.gridwidth - 1) == maxX) {
                    insets.right += bottomBalance;
                }
                if (constraints.gridy == minY) {
                    insets.top += topBalance;
                }
                if (constraints.gridy + (constraints.gridheight - 1) == maxY) {
                    insets.bottom += bottomBalance;
                }
            }
            mLayoutDone = true;
        }
        super.layoutContainer(pContainer);
    }

}
