package org.kari.util.config;

import junit.framework.TestCase;

import org.junit.Test;

public class ConfigTest extends TestCase {

    @Test
    public void testConfig() {
        Config.init("/foobar.properties");
        Config.getInstance().getProperties();
    }

}
