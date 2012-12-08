package org.kari.action;

import java.awt.Window;
import java.lang.reflect.Method;

import org.kari.annotation.Call;


/**
 * Action based into Call -annotation
 * 
 * @author kari
 */
public class KCallAction extends KAction {

    public KCallAction(String pActionName) {
        super(pActionName);
    }
    
    @Override
    public void actionPerformed(ActionContext pCtx)
    {
        final String key = getName();
        final Window window = pCtx.getWindow();
        
        for (Method method : window.getClass().getDeclaredMethods()) {
            Call call = method.getAnnotation(Call.class);
            if (call!=null && key.equals(call.value())) {
                try {
                    method.invoke(window, pCtx);
                } catch (Exception e) {
                    LOG.error("Call failed: " + key, e);
                }
                break;
            }
        }
    }


}
