package org.kari.log;

import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.or.ObjectRenderer;

/**
 * Pretty print array for Log4J
 * @author kari
 */
public class ArrayRenderer implements ObjectRenderer {
    @Override
    public String doRender(Object pArg0) {
        StringBuilder sb = new StringBuilder();
        Object[] array = (Object[])pArg0;
        Map<Object, Object> done = new IdentityHashMap<Object, Object>();
        doRender(array, sb, 0, done);
        return sb.toString();
    }
    
    private final void doRender(
        Object[] pArray, 
        StringBuilder sb, 
        int pLevel,
        Map<Object, Object> pDone) 
    {
        if (pDone.containsKey(pArray)) {
            sb.append("[loop]");
        }
        pDone.put(pArray, pArray);
        sb.append('[');
        int len = pArray.length;
        for (int i = 0; i < len; i++) {
            Object object = pArray[i];
            if (object instanceof List) {
                object = ((List)object).toArray();
            }
            if (object instanceof Object[]) {
                doRender((Object[])object, sb, pLevel + 1, pDone);
            } else {
                sb.append('[');
                sb.append(object);
                sb.append(']');
            }
            if (i < len - 1) {
                sb.append(',');
                sb.append('\n');
            }
        }
        sb.append(']');
    }

}
