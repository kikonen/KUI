package org.kari.io.call.test;

import java.io.Serializable;

/**
 * Result from test API
 *
 * @author kari
 */
public final class TestResult implements Serializable {
    private final String mMessage;

    public TestResult(String pMessage) {
        mMessage = pMessage;
    }

    public TestResult(TestParam pMessage) {
        mMessage = "via-" + pMessage;
    }

    public String getMessage() {
        return mMessage;
    }

    @Override
    public String toString() {
        return mMessage;
    }
        
}
