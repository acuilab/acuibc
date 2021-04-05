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
    public static final String VERSION = "v0.9.17";
    
    // @see conflux.web3j.CfxUnit
    public static final BigInteger CFX_ONE = BigInteger.TEN.pow(18);
    
    public static final String CFX_BLOCKCHAIN_SYMBAL = "CFX";
    public static final String ETH_BLOCKCHAIN_SYMBAL = "ETH";
    public static final String BSC_BLOCKCHAIN_SYMBAL = "BSC";
    
    public static final int TEXT_NOTICE_FONT_SIZE = 12;     // 消息头字体大小
    public static final Color TEXT_NOTICE_COLOR = new Color(0, 128, 64);
    public static final int TEXT_RESULT_FONT_SIZE = 12;     // 消息头字体大小
    public static final Color TEXT_RESULT_COLOR = new Color(0, 0, 0);
    public static final int TEXT_LINE_FONT_SIZE = 12;     // 消息头字体大小
    public static final Color TEXT_LINE_COLOR = new Color(255, 165, 0);
    public static final SimpleAttributeSet TEXT_NOTICE_ATTRIBUTE_SET = new SimpleAttributeSet();
    public static final SimpleAttributeSet TEXT_RESULT_ATTRIBUTE_SET = new SimpleAttributeSet();
    public static final SimpleAttributeSet TEXT_LINE_ATTRIBUTE_SET = new SimpleAttributeSet();
    public static final SimpleAttributeSet LEFT_INDENT_ATTRIBUTE_SET = new SimpleAttributeSet();            // 左缩进
    public static final SimpleAttributeSet RESET_ATTRIBUTE_SET = new SimpleAttributeSet();                  // 重置，左缩进为0，靠左显示
    public static final SimpleAttributeSet ALIGNMENT_CENTER_ATTRIBUTE_SET = new SimpleAttributeSet();       // 居中显示
    public static final SimpleAttributeSet ALIGNMENT_LEFT_ATTRIBUTE_SET = new SimpleAttributeSet();       // 靠左显示
    public static final int LEFT_INDENT = 18;
    
    static {
        // notice
        StyleConstants.setForeground(TEXT_NOTICE_ATTRIBUTE_SET, TEXT_NOTICE_COLOR);
        StyleConstants.setFontSize(TEXT_NOTICE_ATTRIBUTE_SET, TEXT_NOTICE_FONT_SIZE);
        StyleConstants.setBold(TEXT_NOTICE_ATTRIBUTE_SET, true);
        
        // result
        StyleConstants.setForeground(TEXT_RESULT_ATTRIBUTE_SET, TEXT_RESULT_COLOR);
        StyleConstants.setFontSize(TEXT_RESULT_ATTRIBUTE_SET, TEXT_RESULT_FONT_SIZE);
        
        // line
        StyleConstants.setForeground(TEXT_LINE_ATTRIBUTE_SET, TEXT_LINE_COLOR);
        StyleConstants.setFontSize(TEXT_LINE_ATTRIBUTE_SET, TEXT_LINE_FONT_SIZE);

        StyleConstants.setLeftIndent(LEFT_INDENT_ATTRIBUTE_SET, LEFT_INDENT);
        StyleConstants.setAlignment(ALIGNMENT_CENTER_ATTRIBUTE_SET, StyleConstants.ALIGN_CENTER);
        StyleConstants.setAlignment(ALIGNMENT_LEFT_ATTRIBUTE_SET, StyleConstants.ALIGN_LEFT);

        StyleConstants.setLeftIndent(RESET_ATTRIBUTE_SET, 0);
        StyleConstants.setAlignment(RESET_ATTRIBUTE_SET, StyleConstants.ALIGN_LEFT);
    }
}
