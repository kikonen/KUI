package org.kari.action;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;

/**
 * Grouping of actions for radio button groups
 * 
 * @author kari
 */
public class ActionGroup {
    private final Set<KAction> mActions = new HashSet<KAction>();
    /**
     * Map of (action-type, group for actions)
     */
    private Map<String, ButtonGroup> mButtonGroups = new HashMap<String, ButtonGroup>();
    
    public void clear() {
        mActions.clear();
        mButtonGroups.clear();
    }
    
    public Set<KAction> getActions() {
        return mActions;
    }

    public void addAction(KAction pAction) {
        mActions.add(pAction);
    }

    /**
     * @return Selected action, null if none
     */
    public KAction getSelected() {
        for (KAction action : mActions) {
            if (action.isSelected()) {
                return action;
            }
        }
        return null;
    }

    public ButtonGroup getButtonGroup(String pType) {
        ButtonGroup group = mButtonGroups.get(pType);
        if (group == null) {
            group = new ButtonGroup();
            mButtonGroups.put(pType, group);
        }
        return group;
    }
    
}
