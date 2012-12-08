package org.kari.util;

import junit.framework.TestCase;

public class TextUtilTest
    extends TestCase
{
    public void test1() {
        String[] texts = {
            "${user.home}/tmp",
            "/bat/${tmp.dir}/foo"
        };
        
        for (String txt : texts) {
            String value = TextUtil.expand(txt);
            System.out.println("txt=[" + value + "]");
        }

    }
}
