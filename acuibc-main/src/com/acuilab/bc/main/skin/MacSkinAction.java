package com.acuilab.bc.main.skin;

import com.acuilab.bc.main.util.OSinfo;
import javax.swing.UIManager;

/**
 *
 * @author admin
 */
public class MacSkinAction extends SkinAction {

    public MacSkinAction() {
	super("Mac");
	putValue("enabled", OSinfo.isMacOS() || OSinfo.isMacOSX());
    }

    @Override
    public String getSkinClass() {
	return "com.sun.java.swing.plaf.mac.MacLookAndFeel";
    }
    
}
