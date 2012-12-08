package org.kari.util.collection;

import gnu.trove.set.hash.THashSet;

import java.util.Collections;
import java.util.Set;

/**
 * Lazy collection logic: empty and null equal to same
 *
 * @author kari
 */
public final class LazySet {
    /**
     * Set elements
     * 
     * <p>USAGE:
     * <pre>
     * mElements = LazySet.set(elements);
     * </pre>
     * 
     * @param pSet Set or null for lazy
     * @param pElements can be null
     * 
     * @return new Set, null if pElements is null ir empty
     */
    public static <T> Set<T> set(Set<T> pElements) {
        Set<T> result = null;
        if (pElements != null && !pElements.isEmpty()) {
            result = new THashSet<T>(pElements);
        }
        return result;
    }

    /**
     * Add element
     * 
     * <p>USAGE:
     * <pre>
     * mElements = LazySet.add(mElement, elem);
     * </pre>
     * 
     * @param pSet Set or null for lazy
     * @param pElement can be null
     * 
     * @return pSet or new Set, null if pSet is null or pSet is empty
     * and pElement is null
     */
    public static <T> Set<T> add(Set<T> pSet, T pElement) {
        Set<T> result = pSet;
        if (pElement != null) {
            if (result == null) {
                result = new THashSet<T>(3);
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
     * mElements = LazySet.remove(mElement, elem);
     * </pre>
     * 
     * @param pSet Set or null for lazy
     * @param pElement can be null
     * 
     * @return pSet or new Set, null if pSet is null or pSet is empty
     * and pElement is null
     */
    public static <T> Set<T> remove(Set<T> pSet, T pElement) {
        Set<T> result = pSet;
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
     * return LazySet.get(mElements);
     * </pre>
     * 
     * @param pSet Set or null for lazy
     * 
     * @return inmutable Set, can be empty
     */
    public static <T> Set<T> get(Set<T> pElements) {
        return pElements != null && !pElements.isEmpty()
            ? Collections.unmodifiableSet(pElements)
            : Collections.<T>emptySet();
    }

}
