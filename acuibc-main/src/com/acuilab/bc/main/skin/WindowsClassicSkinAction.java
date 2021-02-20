package com.acuilab.bc.main.skin;

import com.acuilab.bc.main.util.OSinfo;

/**
 *
 * @author admin
 */
public class WindowsClassicSkinAction extends SkinAction {

    public WindowsClassicSkinAction() {
	super("Windows Classic");
        putValue("enabled", OSinfo.isWindows());
    }

    @Override
    public String getSkinClass() {
	return "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel";
    }
    
}
