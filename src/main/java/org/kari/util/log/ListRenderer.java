package org.kari.util.log;

import java.util.List;

/**
 * Pretty print List for Log4J
 *
 * @author kari
 */
public class ListRenderer extends ArrayRenderer {
    @Override
    public String doRender(Object pArg0) {
        List list = (List)pArg0;
        return super.doRender(list.toArray());
    }

}
