package org.kari.io.call.test;

import java.io.Serializable;

/**
 * param for test API
 *
 * @author kari
 */
public final class TestParam implements Serializable {
    private final String mMessage;

    public TestParam(String pMessage) {
        mMessage = pMessage;
    }

    public String getMessage() {
        return mMessage;
    }
    
    @Override
    public String toString() {
        return mMessage;
    }
    
}
