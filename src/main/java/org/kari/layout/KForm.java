package org.kari.layout;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;


/**
 * Extra conveniency utility for managing TableLayout
 * 
 * @author kari
 */
public final class KForm extends TableLayoutConstraints {
    public static final int BORDER = 12;
    public static final int GAP = 5;
    
    private final JComponent mComponent;
    
    private TableLayout mLayout;
    private double[] mColumns;
    private double[] mRows;

    public String align;
    private TableLayoutConstraints mFill;

    private int mBorder;
    private int mHGap = GAP;
    private int mVGap = GAP;

    public KForm(JComponent pComponent) {
        mComponent = pComponent;
    }
    
    public KForm(JComponent pComponent, int pBorder) {
        mComponent = pComponent;
        mBorder = pBorder;
    }
    
    public KForm cap(int pGap) {
        return cap(pGap, pGap);
    }

    public KForm cap(int pHGap, int pVGap) {
        mHGap = pHGap;
        mVGap = pVGap;
        return this;
    }
    
    public KForm border(int pBorder) {
        mBorder = pBorder;
        return this;
    }

    
    public KForm cols(double... cols) {
        mColumns = cols;
        return this;
    }
    
    public KForm rows(double... rows) {
        mRows = rows;
        return this;
    }
    
    public KForm label(int pCol, int pRow, String pLabel) {
        return label(pCol, pRow, null, pLabel);
    }
    
    public KForm label(int pCol, int pRow, String pAlign, String pLabel) {
        return add(pCol, pRow, pAlign, new JLabel(pLabel));
    }
    
    public KForm add(int pCol, int pRow, JComponent pComponent) {
        return add(pCol, pRow, null, pComponent);
    }

    public KForm add(int pCol, int pRow, String pAlign, JComponent pComponent) {
        col1 = pCol;
        row1 = pRow;
        col2 = pCol;
        row2 = pRow;
        align = pAlign;
        internalAdd(pComponent);
        return this;
    }

    private void internalAdd(JComponent pComponent) {
        init();
        if (align != null) {
            mFill = new TableLayoutConstraints(col1 + "," + row1 + " " + align);
        } else {
            mFill = new TableLayoutConstraints();
            mFill.col1 = col1;
            mFill.col2 = col2;
            mFill.row1 = row1;
            mFill.row2 = row2;
        }
        mComponent.add(pComponent, mFill);
        reset();
    }

    private void init() {
        if (mLayout == null) {
            mLayout = new TableLayout(mColumns, mRows);
            mLayout.setHGap(mHGap);
            mLayout.setVGap(mVGap);
            mComponent.setLayout(mLayout);
            mComponent.setBorder(BorderFactory.createEmptyBorder(mBorder, mBorder, mBorder, mBorder));
        }
    }

    private void reset() {
        mFill = null;
        align = null;
    }
}
