package org.kari.io.call.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * param for test API
 *
 * @author kari
 */
public final class TestParam implements Serializable {
    private final List<String> mMessage;

    public TestParam(String pMessage) {
        mMessage = new ArrayList<String>();
        mMessage.add(pMessage);
    }

    public String getMessage() {
        return mMessage.get(0);
    }
    
    @Override
    public String toString() {
        return mMessage.get(0);
    }
    
}
