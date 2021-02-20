package com.acuilab.bc.main.util;

import com.acuilab.bc.main.skin.SkinAction;
import java.awt.Component;
import java.awt.Window;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Level;
import javax.swing.SwingUtilities;
import org.openide.util.NbPreferences;

/**
 *
 * @author admin
 */
public class Utils {
    private Utils() {}
    
    public static BigDecimal scaleFloor(BigDecimal decimal, int scale) {
        if(decimal == null) {
            return null;
        }
        
        return decimal.setScale(scale, RoundingMode.FLOOR);
    }
    
    /**
     * 获得当前皮肤类名称
     * @return 
     */
    public static String getSkinClassName() {
	String defaultSkinClassName = "javax.swing.plaf.metal.MetalLookAndFeel";
	if(OSinfo.isMacOS() || OSinfo.isMacOSX()) {
//	    defaultSkinClassName = "com.sun.java.swing.plaf.mac.MacLookAndFeel";
	} else if(OSinfo.isWindows()) {
	    defaultSkinClassName = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
	}
	return NbPreferences.root().get(SkinAction.ROOT_SKIN_KEY, defaultSkinClassName);
    }
    
    /**
     * 应用UI改变
     * @param component 
     */
    public static void applyUIChanges(Object component) {
	if (component instanceof Component) {
	    SwingUtilities.updateComponentTreeUI((Component) component);
	} else if (component == null) {
	    Window windows[] = Window.getWindows();
	    for (Window window : windows) {
		if (window.isDisplayable()) {
		    applyUIChanges(window);
		}
	    }
	}
    }
}
