package org.kari.properties;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.KeyEvent;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import javax.swing.border.EmptyBorder;

import org.kari.action.ActionConstants;
import org.kari.action.ActionContext;
import org.kari.action.KAction;
import org.kari.perspective.KFrame;

/**
 * Base class for properties dialogs
 * 
 * <li>NOTE KI dialog is required to have public constructor
 * 
 * @author kari
 */
public abstract class KPropertiesFrame extends KFrame {
    private JTabbedPane mBook;
    private JPanel mButtonPanel;
    private JComponent mFocusedComponent;
    
    private JFrame mOwner;
    
    
    private final Action mOK = new KAction(ActionConstants.R_OK) {
        @Override
        public void actionPerformed(ActionContext pCtx) {
            LOG.info("Apply");
            try {
                mApply.apply(KPropertiesFrame.this);
                dispose();
            } catch (Exception e) {
                LOG.error("Apply failed");
            }
        }
    };
    
    private final Action mCancel = new KAction(ActionConstants.R_CANCEL) {
        @Override
        public void actionPerformed(ActionContext pCtx) {
            LOG.info("Cancel");
            dispose();
        }
    };
    
    
    private Object mContent;
    private Apply mApply;

    public KPropertiesFrame() {
        setTitle("Properties");

        // Setup root pane listeners
        JRootPane rootPane = getRootPane();
        
        KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        rootPane.registerKeyboardAction(mCancel, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        stroke = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);
        rootPane.registerKeyboardAction(mOK, stroke, JComponent.WHEN_IN_FOCUSED_WINDOW);
        
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(getBook(), BorderLayout.CENTER);
        contentPane.add(getButtonPanel(), BorderLayout.SOUTH);
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        
        setContentPane(contentPane);
        
        setIcon(ActionConstants.R_PROPERTIES);
    }
    
    @Override
    public JFrame getOwner() {
        return mOwner;
    }
    
    public void setOwner(JFrame pOwner) {
        mOwner = pOwner;
    }

    public final JTabbedPane getBook() {
        if (mBook == null) {
            mBook = new JTabbedPane();
        }
        return mBook;
    }
    
    public final JPanel getButtonPanel() {
        if (mButtonPanel == null) {
            mButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            mButtonPanel.add(new JButton(mOK));
            mButtonPanel.add(new JButton(mCancel));
        }
        return mButtonPanel;
    }
    
    @Override
    @Deprecated
    public void show(boolean pVisible) {
        super.show(pVisible);
        if (pVisible) {
            if (mFocusedComponent != null) {
                mFocusedComponent.requestFocus();
            }
        }
    }
    
    public final void addPage(String pTitle, JComponent pPage) {
        getBook().addTab(pTitle, pPage);
    }
    
    public final Apply getApply() {
        return mApply;
    }

    public final void setApply(Apply pApply) {
        mApply = pApply;
    }

    /**
     * @return Initially focused component, null if not specified
     */
    public JComponent getFocusedComponent() {
        return mFocusedComponent;
    }

    /**
     * Set component which will get focus when dialog is shown
     */
    public void setFocusedComponent(JComponent pFocusedComponent) {
        mFocusedComponent = pFocusedComponent;
    }

    public void setContent(Object pContent) 
        throws Exception
    {
        mContent = pContent;
    }

    public Object getContent() 
        throws Exception
    {
        return mContent;
    }
}
