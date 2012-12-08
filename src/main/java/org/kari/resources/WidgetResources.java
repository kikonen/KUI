package org.kari.resources;

import java.awt.event.KeyEvent;

import javax.swing.Icon;
import javax.swing.KeyStroke;
import javax.swing.UIManager;

import org.apache.log4j.Logger;

/**
 * Resources for specific widget type
 */
public final class WidgetResources {
    private static final Logger LOG = Logger.getLogger("ki.util.resources");
    
    final String mKey;
    final String mType;
    private String mText;
    private String mTip;
    private int mMenomnicIndex = -1;
    private Icon mIcon;
    private KeyStroke mAccelerator;
    
    WidgetResources(
        final String pKey, 
        final String pType, 
        final ResourceAdapter pAdapter) 
    {
        mKey = pKey;
        mType = pType;
        initialize(pAdapter);
    }
    
    private void initialize(final ResourceAdapter pAdapter) {
        mText = (String)pAdapter.get(mType + ResKey.TEXT + mKey);
        if (mText == null) {
            mText = (String)pAdapter.get(ResKey.TEXT + mKey);
        }
        if (mText == null) {
            mText = mKey;
        }

        mTip = (String)pAdapter.get(mType + ResKey.TIP + mKey);
        if (mTip == null) {
            mTip = (String)pAdapter.get(ResKey.TIP + mKey);
        }

        String acc = (String)pAdapter.get(ResKey.ACC + mKey);
        if (acc != null) {
            mAccelerator = KeyStroke.getKeyStroke(acc);
            if (mAccelerator == null) {
                LOG.warn("Invalid: " + ResKey.ACC + mKey + "=" + acc);
            }
        }
        
        String iconURL = (String)pAdapter.get(mType + ResKey.ICON + mKey);
        if (iconURL == null) {
            iconURL = (String)pAdapter.get(ResKey.ICON + mKey);
        }
        if (iconURL != null) {
            mIcon = ResUtil.getIcon(iconURL);
        }
        mMenomnicIndex = ResUtil.getMnemonicIndex(mText);
        mText = ResUtil.getText(mText, mMenomnicIndex);
        
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj==this)
            return true;
        if (obj==null)
            return false;
        if (obj.getClass()==getClass()) {
            final WidgetResources wr = (WidgetResources)obj;
            return mKey.equals(wr.mKey)
                && mType.equals(wr.mType);
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return mKey.hashCode();
    }

    @Override
    public String toString() {
        return "Widget:" + mKey;
    }
    
    public String getText() {
        return mText;
    }
    
    /**
     * @return Index of mnemonics, -1 if not defined
     */
    public int getMnenomnicIndex() {
        return mMenomnicIndex;
    }
    
    public char getMnenomnic() {
        return mMenomnicIndex!=-1 ? mText.charAt(mMenomnicIndex) : 0;
    }

    public Icon getIcon() {
        return mIcon;
    }

    public KeyStroke getAccelerator() {
        return mAccelerator;
    }
    
    public String getTip() {
        return mTip;
    }

    public String getToolTip() {
        String tip = mTip;
        if (tip == null) {
            tip = mText;
        }
        String accText = null;
        KeyStroke acc = mAccelerator;
        if (acc != null) {
            int modifiers = acc.getModifiers();
            if (modifiers > 0) {
                accText = KeyEvent.getKeyModifiersText(modifiers);
                String accDelim = UIManager.getString("MenuItem.acceleratorDelimiter");
                if (accDelim == null) { 
                    accDelim = "+"; 
                }
                accText += accDelim;
            }
            int keyCode = acc.getKeyCode();
            if (keyCode != 0) {
                accText += KeyEvent.getKeyText(keyCode);
            } else {
                accText += acc.getKeyChar();
            }
        }
        
        if (accText != null) {
            tip += " (" + accText + ")";
        }

        return tip;
    }
}
