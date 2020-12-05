package com.acuilab.bc.main.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

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
}
