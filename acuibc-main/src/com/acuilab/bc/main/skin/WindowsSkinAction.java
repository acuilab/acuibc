package com.acuilab.bc.main.skin;

import com.acuilab.bc.main.util.OSinfo;

/**
 *
 * @author admin
 */
public class WindowsSkinAction extends SkinAction {

    public WindowsSkinAction() {
	super("Windows");
        putValue("enabled", OSinfo.isWindows());
    }

    @Override
    public String getSkinClass() {
	return "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    }
    
}
