package com.acuilab.bc.main.util;

import com.acuilab.bc.main.skin.SkinAction;
import java.awt.Component;
import java.awt.Window;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javax.swing.SwingUtilities;
import org.apache.commons.lang3.StringUtils;
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
    
    /***
     * 简化哈希值，首尾各保留reservedCount位，中间部分用...代替
     * @param str
     * @param reservedCount
     * @return 
     */
    public static String simplifyString(String str, int reservedCount) {
	int length = StringUtils.length(str);
	if(length > 2*reservedCount) {
	    return StringUtils.substring(str, 0, reservedCount) + "..." + StringUtils.substring(str, length-reservedCount);
	}
	
	return str;
    }
    
    /**
     * 字节转换
     * @param size
     * @return 
     */
    public static String getPrintSize(long size) {
        //如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        if (size < 1024) {
            return String.valueOf(size) + "B";
        } else {
            size = size / 1024;
        }
        //如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        //因为还没有到达要使用另一个单位的时候
        //接下去以此类推
        if (size < 1024) {
            return String.valueOf(size) + "KB";
        } else {
            size = size / 1024;
        }
        if (size < 1024) {
            //因为如果以MB为单位的话，要保留最后1位小数，
            //因此，把此数乘以100之后再取余
            size = size * 100;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "MB";
        } else {
            //否则如果要以GB为单位的，先除于1024再作同样的处理
            size = size * 100 / 1024;
            return String.valueOf((size / 100)) + "."
                    + String.valueOf((size % 100)) + "GB";
        }
    }
}
