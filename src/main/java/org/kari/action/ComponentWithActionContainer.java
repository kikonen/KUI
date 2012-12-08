package org.kari.action;

/**
 * Allow to access action container of component
 *
 * @author kari
 */
public interface ComponentWithActionContainer {
    /**
     * Get action mappings for component
     */
    ActionContainer getActionContainer();
}
