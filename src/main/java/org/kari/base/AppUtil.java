package org.kari.base;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.apache.log4j.Logger;
import org.kari.action.ActionResources;
import org.kari.resources.ResourceAdapter;
import org.kari.util.SystemUtil;
import org.kari.util.log.LogUtil;

/**
 * Utility for starting application
 * 
 * @author kari
 */
public class AppUtil {
    public static final String NIMBUS_LF = "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel";
    

    protected static final Logger LOG = Logger.getLogger("ki.base");
    
    /**
     * @param pType Type, which is JFrame instance
     */
    public static void start(final Class pType, final String... pArgs) {
        LogUtil.initialize();
        ResourceAdapter.getInstance().addBundle(ActionResources.class);

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Object app = pType.newInstance();
                    if (app instanceof Application) {
                        ((Application)app).start(new CommandLine(pArgs));
                    } else {
                        ((JFrame)app).setVisible(true);
                    }
                } catch (Exception e) {
                    LOG.error("Failed to create: " + pType, e);
                    System.exit(-1);
                }
            }
        });
    }

    public static void setLF() {
        // Initialize look and feel; if preferred is not available then use default
        try {
            UIManager.setLookAndFeel(NIMBUS_LF);
        } catch (Exception e) {
            LOG.error(NIMBUS_LF + " not available", e);
            if (SystemUtil.isWindows()) {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception e1) {
                    // This is really bad
                    LOG.fatal("System LF failed", e);
                }
            } else {
                System.setProperty("swing.plaf.metal.controlFont", "Dialog");
            }
        }
    }
}
