package org.kari.call.test;

/**
 * Testing exception throwing
 */
public class TestException extends Exception {
    public TestException() {
        super();
    }

    public TestException(String pMessage) {
        super(pMessage);
    }
}
