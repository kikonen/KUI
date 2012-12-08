package org.kari.base;

/**
 * Base registry for unique classIds
 * 
 * Format:
 * 
 * 76543210 76543210 76543210 76543210 
 * PPPPPPPP PPMMMMMM MMMMTTTT KKKKKKKK
 * 
 * 0  -  7    Key            8 bits
 * 8  - 11    Type        4 bits
 * 12 - 21    Module        10 bits
 * 22 - 31    Project        10 bits
 * 
 */
public interface ClassId {

    int SHIFT_PROJECT = 22;
    int SHIFT_MODULE = 12;
    int SHIFT_TYPE = 8;
}
