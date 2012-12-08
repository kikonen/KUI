package org.kari.layout;

import java.awt.Component;
import java.awt.Dimension;

/**
 * Simple filler component
 * 
 * @author kari
 */
public class Strut extends Component {
    private final int mSize;

    public Strut(int pSize) {
        mSize = pSize;
        Dimension dim = new Dimension(mSize, mSize);
        setMinimumSize(dim);
        setMaximumSize(dim);
        setPreferredSize(dim);
    }
    
}
