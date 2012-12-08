package org.kari.resources;

/**
 * Interface for allowing quick parse of ListResourceBundle contents
 */
public interface QuickBundle extends ResKey {

    Object[][] getContents();
}
