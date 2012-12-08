package org.kari.widget;

import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.kari.base.Application;
import org.kari.base.CommandLine;

public abstract class KAppFrame extends JFrame 
    implements
        Application
{
    private CommandLine mArgs;
    
    public KAppFrame()
    {
        // Nothing
    }

    public void start(CommandLine pArgs) 
        throws Exception
    {
        mArgs = pArgs;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        create();
        setVisible(true);
    }
    

    protected void create()
        throws Exception 
    {
        setContentPane(createContentPanel());
        pack();
        Dimension size = getSize();
        if (size.width < 200) {
            size.width = 200;
        }
        if (size.height < 100) {
            size.height = 100;
        }
        setSize(size);
    }
    
    protected abstract JComponent createContentPanel()
        throws Exception;
    

    public CommandLine getArgs() {
        return mArgs;
    }

    public void setArgs(CommandLine pArgs) {
        mArgs = pArgs;
    }
    
}
