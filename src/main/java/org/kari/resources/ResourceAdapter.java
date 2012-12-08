package org.kari.resources;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * Manage shared resources
 */
public final class ResourceAdapter {

    private static final Logger LOG = Logger.getLogger("ki.resources");

    private static final Set<String> mBundleNames = new HashSet<String>();
    private static final Map<String, Object> mCache = new HashMap<String, Object>();
    private static final Map<String, WidgetResources> mWidgets = new HashMap<String, WidgetResources>();
    
    private static ResourceAdapter mInstance;
    
    /**
     * Utility/Singleton
     */
    private ResourceAdapter() {
        // Nothing
    }
    

    public static ResourceAdapter getInstance() {
        if (mInstance==null) {
            mInstance = new ResourceAdapter();
        }
        return mInstance;
    }
    

    /**
     * Register resourcebundle
     * 
     * @param pBundle
     */
    public void addBundle(final Class pBundle) {
        synchronized (mBundleNames) {
            mBundleNames.add(pBundle.getName());
        }
    }

    /**
     * Get individual resource
     * 
     * @param pKey
     * @return Resource value, null if not found
     */
    public Object get(final Object pKey) {
        Object result = null;
        synchronized (mCache) {
            if (mCache.isEmpty()) {
                initialize();
            }
            result = mCache.get(pKey);
        }
        return result;
    }

    
    public WidgetResources getWidget(final String pActionName, final String pType) {
        WidgetResources wr;
        synchronized (mWidgets) {
            String key = pType + pActionName;
            wr = mWidgets.get(key);
            if (wr==null) {
                wr = new WidgetResources(pActionName, pType, this);
                mWidgets.put(key, wr);
            }
        }
        return wr;
    }
    
    
    public boolean contains(final Object pKey) {
        synchronized (mCache) {
            if (!mCache.isEmpty()) {
                initialize();
            }
            return mCache.containsKey(pKey);
        }
    }

    private void initialize() {
        synchronized (mBundleNames) {
            final Iterator bundleIter = mBundleNames.iterator();
            
            while (bundleIter.hasNext()) {
                final String bundleName = (String)bundleIter.next();
                
                try {
                    final QuickBundle bundle = (QuickBundle)ResourceBundle.getBundle(bundleName);
                    final Object[][] contents = bundle.getContents();
                    
                    for (int i=0; i < contents.length; i++) {
                        final Object[] row = contents[i];
                        mCache.put((String)row[0], row[1]);
                    }
                } catch (MissingResourceException e) {
                    LOG.error("failed to load bundle: " + bundleName, e);
                }
            }
        }
    }
}
