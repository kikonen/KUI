package org.kari.util.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Lazy collection logic: empty and null equal to same
 *
 * @author kari
 */
public final class LazyList {
    /**
     * Set elements
     * 
     * <p>USAGE:
     * <pre>
     * mElements = LazyList.set(elements);
     * </pre>
     * 
     * @param pList list or null for lazy
     * @param pElements can be null
     * 
     * @return new list, null if pElements is null ir empty
     */
    public static <T> List<T> set(List<T> pElements) {
        List<T> result = null;
        if (pElements != null && !pElements.isEmpty()) {
            result = new ArrayList<T>(pElements);
        }
        return result;
    }

    /**
     * Add element
     * 
     * <p>USAGE:
     * <pre>
     * mElements = LazyList.add(mElement, elem);
     * </pre>
     * 
     * @param pList list or null for lazy
     * @param pElement can be null
     * 
     * @return pList or new list, null if pList is null or pList is empty
     * and pElement is null
     */
    public static <T> List<T> add(List<T> pList, T pElement) {
        List<T> result = pList;
        if (pElement != null) {
            if (result == null) {
                result = new ArrayList<T>(3);
            }
            result.add(pElement);
        } else {
            if (result != null && result.isEmpty()) {
                result = null;
            }
        }
        return result;
    }
    
    /**
     * Remove element
     * 
     * <p>USAGE:
     * <pre>
     * mElements = LazyList.remove(mElement, elem);
     * </pre>
     * 
     * @param pList list or null for lazy
     * @param pElement can be null
     * 
     * @return pList or new list, null if pList is null or pList is empty
     * and pElement is null
     */
    public static <T> List<T> remove(List<T> pList, T pElement) {
        List<T> result = pList;
        if (pElement != null) {
            if (result != null) {
                result.remove(pElement);
            }
        } else {
            if (result != null && result.isEmpty()) {
                result = null;
            }
        }
        return result;
    }

    /**
     * Get elements
     * 
     * <p>USAGE:
     * <pre>
     * return LazyList.get(mElements);
     * </pre>
     * 
     * @param pList list or null for lazy
     * 
     * @return inmutable list, can be empty
     */
    public static <T> List<T> get(List<T> pElements) {
        return pElements != null && !pElements.isEmpty()
            ? Collections.unmodifiableList(pElements)
            : Collections.<T>emptyList();
    }

}
