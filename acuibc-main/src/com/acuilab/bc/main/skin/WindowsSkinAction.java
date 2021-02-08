package com.acuilab.bc.main.skin;

/**
 *
 * @author admin
 */
public class WindowsSkinAction extends SkinAction {

    public WindowsSkinAction() {
	super("Windows");
    }

    @Override
    public String getSkinClass() {
	return "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    }
    
}
