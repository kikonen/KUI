package org.kari.base;

/**
 * Type Ids
 */
public interface TypeId extends ClassId {

    int CLASS        = 1 << SHIFT_TYPE;
    int VIEW         = 2 << SHIFT_TYPE;
    int RESOURCE    = 3 << SHIFT_TYPE;
    int ACTION        = 4 << SHIFT_TYPE;
    int MENU        = 5 << SHIFT_TYPE;
    int TOOLBAR        = 6 << SHIFT_TYPE;
    
}
