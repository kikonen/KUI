package org.kari.action.std;

import org.kari.action.ActionConstants;
import org.kari.action.ActionContext;
import org.kari.action.KAction;

/**
 * Standard action to exit application
 * 
 * @author kari
 */
public class ExitAction extends KAction {

    public ExitAction() {
        super(ActionConstants.R_EXIT);
    }

    @Override
    public void actionPerformed(ActionContext pCtx) {
        System.exit(0);
    }
    
}
