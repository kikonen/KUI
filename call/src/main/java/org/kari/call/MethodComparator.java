package org.kari.call;

import java.lang.reflect.Method;
import java.util.Comparator;

/**
 * Sort methods to generate indexing for them
 *
 * @author kari
 */
public final class MethodComparator implements Comparator<Method> {
    public static final MethodComparator INSTANCE = new MethodComparator();

    @Override
    public int compare(Method a, Method b) {
        int result = a.getName().compareTo(b.getName());

        // name is same; operator overloading by argument count
        if (result == 0) {
            // less args is first
            final Class<?>[] typesA = a.getParameterTypes();
            final Class<?>[] typesB = b.getParameterTypes();
            
            result = typesA.length - typesB.length;
            
            // argument count is same; operator overloading by argument type
            if (result == 0) {
                // check argument types; they must differ
                for (int i = 0; result == 0 && i < typesA.length; i++) {
                    result = typesA[i].getName().compareTo(typesB[i].getName());
                }
            }
        }
        
        return result;
    }
    
}
