package com.acuilab.bc.main.skin;

/**
 *
 * @author admin
 */
public class WindowsClassicSkinAction extends SkinAction {

    public WindowsClassicSkinAction() {
	super("Windows Classic");
    }

    @Override
    public String getSkinClass() {
	return "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel";
    }
    
}
