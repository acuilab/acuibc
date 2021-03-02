package com.acuilab.bc.main.util;

import java.awt.Color;
import java.math.BigInteger;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 *
 * @author admin
 */
public class Constants {
    private Constants() {}
    
    public static final String TITLE = "宝葫芦Gourd — Conflux社区桌面钱包";
    public static final String VERSION = "v0.9.12";
    
    // @see conflux.web3j.CfxUnit
    public static final BigInteger CFX_ONE = BigInteger.TEN.pow(18);
    
    public static final String CFX_BLOCKCHAIN_SYMBAL = "CFX";
    public static final String ETH_BLOCKCHAIN_SYMBAL = "ETH";
    
    public static final int TEXT_HEADER_FONT_SIZE = 16;     // 消息头字体大小
    public static final Color TEXT_HEADER_COLOR = new Color(0, 128, 64);
    public static final SimpleAttributeSet TEXT_HEADER_ATTRIBUTE_SET = new SimpleAttributeSet();
    public static final SimpleAttributeSet LEFT_INDENT_ATTRIBUTE_SET = new SimpleAttributeSet();            // 左缩进
    public static final SimpleAttributeSet RESET_ATTRIBUTE_SET = new SimpleAttributeSet();                  // 重置，左缩进为0，靠左显示
    public static final SimpleAttributeSet ALIGNMENT_CENTER_ATTRIBUTE_SET = new SimpleAttributeSet();       // 居中显示
    public static final int LEFT_INDENT = 18;

    static {
        StyleConstants.setForeground(TEXT_HEADER_ATTRIBUTE_SET, TEXT_HEADER_COLOR);
        StyleConstants.setFontSize(TEXT_HEADER_ATTRIBUTE_SET, TEXT_HEADER_FONT_SIZE);

        StyleConstants.setLeftIndent(LEFT_INDENT_ATTRIBUTE_SET, LEFT_INDENT);
        StyleConstants.setAlignment(ALIGNMENT_CENTER_ATTRIBUTE_SET, StyleConstants.ALIGN_CENTER);

        StyleConstants.setLeftIndent(RESET_ATTRIBUTE_SET, 0);
        StyleConstants.setAlignment(RESET_ATTRIBUTE_SET, StyleConstants.ALIGN_LEFT);
    }
}
