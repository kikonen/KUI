package org.kari.action;

import java.util.ListResourceBundle;

import org.kari.resources.QuickBundle;
import org.kari.resources.ResKey;

/**
 * Default action resources
 * 
 * @author kari
 */
public class ActionResources
    extends ListResourceBundle
    implements
        QuickBundle,
        ActionConstants,
        ResKey
{
    private static final Object[][] CONTENTS = {
        { TEXT + R_MENU_FILE, "&File" },
        { TEXT + R_MENU_EDIT, "&Edit" },
        { TEXT + R_MENU_VIEW, "&View" },
        { TEXT + R_MENU_OPTION, "&Option" },
        { TEXT + R_MENU_HELP, "&Help" },

        { TEXT + R_NEW, "&New" },
        { ICON + R_NEW, "/icon/sun/New16.gif" },
        { T_ICON + R_NEW, "/icon/sun/New24.gif" },
        { ACC + R_NEW, "ctrl N" },

        { TEXT + R_OPEN, "&Open" },
        { ICON + R_OPEN, "/icon/sun/Open16.gif" },
        { T_ICON + R_OPEN, "/icon/sun/Open24.gif" },
        { ACC + R_OPEN, "ctrl O" },

        { TEXT + R_SAVE, "&Save" },
        { ICON + R_SAVE, "/icon/sun/Save16.gif" },
        { T_ICON + R_SAVE, "/icon/sun/Save24.gif" },
        { ACC + R_SAVE, "ctrl S" },

        { TEXT + R_SAVE_ALL, "Save &All" },
        { ICON + R_SAVE_ALL, "/icon/sun/SaveAll16.gif" },
        { T_ICON + R_SAVE_ALL, "/icon/sun/SaveAll24.gif" },

        { TEXT + R_PROPERTIES, "&Properties" },
        { ICON + R_PROPERTIES, "/icon/sun/Properties16.gif" },
        { T_ICON + R_PROPERTIES, "/icon/sun/Properties24.gif" },
        { ACC + R_PROPERTIES, "ctrl R" },
        
        { TEXT + R_OK, "OK" },
        
        { TEXT + R_APPLY, "&Apply" },
        
        { TEXT + R_CANCEL, "Cancel" },
        { ACC + R_CANCEL, "ESCAPE" },

        { TEXT + R_COPY, "&Copy" },
        { ICON + R_COPY, "/icon/sun/Copy16.gif" },
        { T_ICON + R_COPY, "/icon/sun/Copy24.gif" },
        { ACC + R_COPY, "ctrl C" },

        { TEXT + R_CUT, "Cu&t" },
        { ICON + R_CUT, "/icon/sun/Cut16.gif" },
        { T_ICON + R_CUT, "/icon/sun/Cut24.gif" },
        { ACC + R_CUT, "ctrl X" },

        { TEXT + R_PASTE, "&Paste" },
        { ICON + R_PASTE, "/icon/sun/Paste16.gif" },
        { T_ICON + R_PASTE, "/icon/sun/Paste24.gif" },
        { ACC + R_PASTE, "ctrl V" },

        { TEXT + R_CLEAR, "&Clear" },

        { TEXT + R_REMOVE, "&Remove" },
        { ICON + R_REMOVE, "/icon/sun/Remove16.gif" },
        { T_ICON + R_REMOVE, "/icon/sun/Remove24.gif" },
        { ACC + R_REMOVE, "DELETE" },

        { TEXT + R_OPEN_EXPLORER, "&Explorer" },
        { ACC + R_OPEN_EXPLORER, "f11" },

        { TEXT + R_OPEN_LOG, "&Logs" },
        
        { TEXT + R_CLOSE, "&Close" },
        { ACC + R_CLOSE, "alt F4" },

        { TEXT + R_EXIT, "E&xit" },
        { ACC + R_EXIT, "ctrl Q" },
    };
    
    @Override
    public Object[][] getContents() {
        return CONTENTS;
    }

}
