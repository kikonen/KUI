package org.kari.base;

/**
 * Reserved project IDs
 * 
 * RESERVED
 * ----------------------------
 * 0 - 9   System Internal
 * ----------------------------
 */
public interface ProjectId extends ClassId {

    int BASE_PROJECT = 10;
    
    int PROJECT_TEST = (BASE_PROJECT + 1) << SHIFT_PROJECT;

}
