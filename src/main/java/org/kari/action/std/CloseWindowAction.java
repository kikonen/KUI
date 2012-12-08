package org.kari.action.std;

import java.awt.Window;

import org.kari.action.ActionConstants;
import org.kari.action.ActionContext;
import org.kari.action.KAction;

/**
 * Standard action to exit application
 * 
 * @author kari
 */
public class CloseWindowAction extends KAction {

    public CloseWindowAction() {
        super(ActionConstants.R_CLOSE);
    }

    @Override
    public void actionPerformed(ActionContext pCtx) {
        Window window = pCtx.getWindow();
        window.dispose();
    }
    
}
