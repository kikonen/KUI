package org.kari.util;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import org.apache.log4j.Logger;

/**
 * Misc system utilities
 * 
 * @author kari
 */
public class SystemUtil {
    private static final Logger LOG = Logger.getLogger("ki.util.system");

    /**
     * Get current process PID
     * 
     * @return pid, -1 if resolving failed
     */
    public static int getPID() {
        int pid = -1;
        try {
            RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
            String id = runtime.getName();
            int idx = id.indexOf('@');
            if (idx != -1) {
                id = id.substring(0, idx);
            }
            pid = Integer.parseInt(id);
        } catch (Exception e) {
            LOG.warn("Failed to resolve PID", e);
        }
        return pid;
    }

    public static boolean isLinux() {
        return System.getProperty("os.name").toLowerCase().indexOf("linux") != -1;
    }
    
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().indexOf("windows") != -1;
    }

}
