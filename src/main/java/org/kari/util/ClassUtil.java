package org.kari.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.kari.log.LogUtil;


public final class ClassUtil {
    private static final Logger LOG = LogUtil.getLogger("classutil");

    /**
     * Utility class
     */
    private ClassUtil() {
        // Nothing
    }
    
    /**
     * Collect all interfaces for given type
     */
    public static Class[] collectInterfaces(Class pClass) {
        List<Class> types = new ArrayList<Class>();
        Set<Class> done = new HashSet<Class>();
        
        collectInterfaces(pClass, types, done);
        return types.toArray(new Class[types.size()]);
    }
    
    private static void collectInterfaces(Class pClass, List<Class> pResult, Set<Class> pDone) {
        if (!pDone.contains(pClass)) {
            if (pClass.isInterface()) {
                pResult.add(pClass);
            }
            pDone.add(pClass);

            Class[] interfaces = pClass.getInterfaces();
            if (interfaces!=null) {
                for (Class cls : interfaces) {
                    collectInterfaces(cls, pResult, pDone);
                }
            }
            
            Class cls = pClass.getSuperclass();
            while (cls!=null) {
                collectInterfaces(pClass, pResult, pDone);
                cls = cls.getSuperclass();
            }
        }
    }

/**
 * @return True if current execution points originates from own classes
 */
public static boolean isOwn() {
    StackTraceElement[] stack = Thread.currentThread().getStackTrace();
    
    int len = stack.length;
    for (int i = 2; i < len; i++) {
        if (stack[i].getClassName().startsWith("org.kari.")) {
            return true;
        }
    }
    
    return false;
}

/**
 * @return true if StringBuilder is directly called by own class
 */
public static boolean isImmediateOwn() {
    StackTraceElement[] stack = Thread.currentThread().getStackTrace();

    int index = 2;
    int len = stack.length;
    
    for (; index < len; index++) {
        String className = stack[index].getClassName();
        if (className.startsWith("org.kari.")) {
            return true;
        }
        if (!(className.equals("java.lang.AbstractStringBuilder")
            || className.equals("java.lang.StringBuilder")) ) 
        {
            return false;
        }
    }
    
    return false;
}

    /**
     * Invoke static method of given class
     */
    public static Object invokeStatic(
        Class pClass,
        String pMethodName,
        Class[] pParamTypes,
        Object... pParam)
        throws Exception
    {
        Method method = pClass.getDeclaredMethod(pMethodName, pParamTypes);
        return method.invoke(null, pParam);
    }

    public static <T> T clone(T pObject) {
        return clone(pObject, true);
    }
    
    public static <T> T clone(T pObject, boolean pCompact) {
        T result = null;
        if (pObject != null) {
            byte[] data = serialize(pObject, pCompact);
            LOG.info("cloned size=" + data.length);
            result = deserialize(data, pCompact);
        }
        return result;
    }
    
    public static byte[] serialize(Object pObject, boolean pCompact) {
        byte[] result = null;
        if (pObject != null) {
            try {
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                ObjectOutputStream out = pCompact
                    ? new CompactObjectOutputStream(buffer)
                    : new ObjectOutputStream(buffer);
                out.writeObject(pObject);
                out.close();
                result = buffer.toByteArray();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    public static <T> T deserialize(byte[] pData, boolean pCompact) {
        Object result = null;
        if (pData != null) {
            try {
                ByteArrayInputStream buffer = new ByteArrayInputStream(pData);
                ObjectInputStream in = pCompact
                    ? new CompactObjectInputStream(buffer)
                    : new ObjectInputStream(buffer);
                result = in.readObject();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return (T)result;
    }

}
