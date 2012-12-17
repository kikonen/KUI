package org.kari.util;

import junit.framework.TestCase;

import org.junit.Test;

public class ExpandVarTest extends TestCase {

    @Test
    public void testExpand() {
        assertEquals(
                "null boo bar",
                new ExpandVar().expand("${zoo} boo bar", true));
        assertEquals(
                "zoo null bar",
                new ExpandVar().expand("zoo ${foo} bar", true));
        assertEquals(
                "zoo null null",
                new ExpandVar().expand("zoo ${foo} ${bar}", true));
        assertEquals(
                "zoo ${foo ${bar",
                new ExpandVar().expand("zoo ${foo ${bar", true));
    }

}
