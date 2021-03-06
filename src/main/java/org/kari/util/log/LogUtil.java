package org.kari.util.log;

import java.awt.datatransfer.DataFlavor;
import java.util.List;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Hierarchy;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggerRepository;

/**
 * Logger utilities
 */
public abstract class LogUtil {
    static {
        initializeImpl();
    }

    private static void initializeImpl() {
        BasicConfigurator.configure();
        
        LoggerRepository repo = LogManager.getLoggerRepository();
        if (repo instanceof Hierarchy) {
            Hierarchy hierarchy = (Hierarchy) repo;
            ArrayRenderer ar = new ArrayRenderer();
            hierarchy.addRenderer(Object[].class, ar);
            hierarchy.addRenderer(String[].class, ar);
            hierarchy.addRenderer(Class[].class, ar);
            hierarchy.addRenderer(DataFlavor[].class, ar);
            hierarchy.addRenderer(List.class, new ListRenderer());
        }
    }
    
    public static void initialize() {
        // Nothing
    }

    public static Logger getLogger(String pName) {
        return Logger.getLogger(pName);
    }
}
