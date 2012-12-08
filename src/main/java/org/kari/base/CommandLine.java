package org.kari.base;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility for parsing command line arguments
 * 
 * @author kari
 */
public final class CommandLine {

    private String[] mArgs;

    private final Map<String, String> mParsed = new HashMap<String, String>();

    public CommandLine(String[] pArgs) {
        mArgs = pArgs;
        if (mArgs == null) {
            mArgs = new String[0];
        }
        parse();
    }

    public String[] getArgs() {
        return mArgs;
    }

    private void parse() {
        if (mArgs != null) {
            int idx = 0;
            while (idx < mArgs.length) {
                String arg = mArgs[idx];
                if (arg.startsWith("-")) {
                    // Option
                    mParsed.put(arg.substring(1), "true");
                } else {
                    int assign = arg.indexOf('=');
                    if (assign != -1) {
                        mParsed.put(arg.substring(0, assign), arg.substring(assign + 1));
                    }
                }
                idx++; 
            }
        }
    }
    
    public boolean getBoolean(String pArg) {
        Object value = mParsed.get(pArg);
        return Boolean.parseBoolean( (String)value );
    }
    
    public String getString(String pArg) {
        Object value = mParsed.get(pArg);
        return (String)value;
    }

}
