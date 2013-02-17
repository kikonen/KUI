package org.kari.io.call.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Result from test API
 *
 * @author kari
 */
public final class TestResult implements Serializable {
    private final List<String> mMessage;

    public TestResult(String pMessage) {
        mMessage = new ArrayList<String>();
        mMessage.add(pMessage);
    }

    public TestResult(TestParam pMessage) {
        this("via-" + pMessage);
    }

    public String getMessage() {
        return mMessage.get(0);
    }

    @Override
    public String toString() {
        return mMessage.get(0);
    }
        
}
