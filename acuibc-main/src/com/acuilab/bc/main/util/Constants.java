package com.acuilab.bc.main.util;

import java.awt.Color;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

/**
 *
 * @author admin
 */
public class Constants {
    private Constants() {}
    
    public static final String TITLE = "宝葫芦Gourd — Conflux社区桌面钱包";
    public static final String VERSION = "v0.9.27";
    
    // @see conflux.web3j.CfxUnit
    public static final BigInteger CFX_ONE = BigInteger.TEN.pow(18);
    
    public static final String CFX_BLOCKCHAIN_SYMBAL = "CFX";
    public static final String ETH_BLOCKCHAIN_SYMBAL = "ETH";
    public static final String BSC_BLOCKCHAIN_SYMBAL = "BSC";
    
    public static final String CFX_CFX_SYMBOL = "CFX";
    public static final String CFX_FC_SYMBOL = "FC";
    public static final String CFX_MOON_SYMBOL = "MOON";
    public static final String CFX_YAO_SYMBOL = "YAO";
    public static final String CFX_TREA_SYMBOL = "TREA";
    public static final String CFX_FLUX_SYMBOL = "Flux";
    public static final String CFX_ITF_SYMBOL = "ITF";
    public static final String CFX_POOLGO_SYMBOL = "PoolGo";
    public static final String CFX_YAO_CFX_PAIR_SYMBOL = "YAO_CFX";
    public static final String CFX_DAN_SYMBOL = "DAN";

    public static final Map<String, String> CFX_ADDRESS_SYMBOL_MAP = new HashMap<String, String>() {{
        put("cfx:acg158kvr8zanb1bs048ryb6rtrhr283ma70vz70tx", CFX_CFX_SYMBOL);
        put("cfx:achc8nxj7r451c223m18w2dwjnmhkd6rxawrvkvsy2", CFX_FC_SYMBOL);
        put("cfx:achcuvuasx3t8zcumtwuf35y51sksewvca0h0hj71a", CFX_MOON_SYMBOL);
        put("cfx:acbyzcbfpymaz43rr6s1gtx0fb08guj88uzc05rchf", CFX_DAN_SYMBOL);
        put("cfx:acaucwuza1nm7wfj1bwkjttz7b0eh4ak7ur7fue1dy", CFX_YAO_SYMBOL);
        put("cfx:acb9wkgbefcja9rkpds5ve4cm5643jmebae7xjzz8f", CFX_TREA_SYMBOL);
        put("cfx:acgbjtsmfpex2mbn97dsygtkfrt952sp0psmh8pnvz", CFX_FLUX_SYMBOL);
        put("cfx:acc8599utu7nayj50w393eycznhv4e23g2ys6xmvf5", CFX_ITF_SYMBOL);
        put("cfx:acc8ya1f2a2bfphxg5ax7a8h29k47d5xsebxfj24nd", CFX_POOLGO_SYMBOL);
    }};
    
    public static final int GUGUO_KAOZI_PID = 0;
    public static final int GUGUO_MOON_PID = 1;
    public static final int GUGUO_FLUX_PID = 2;
    public static final int GUGUO_GUGUO_PID = 3;
    
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
