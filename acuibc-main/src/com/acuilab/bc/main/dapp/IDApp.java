package com.acuilab.bc.main.dapp;

import javax.swing.ImageIcon;

/**
 *
 * @author admin
 */
public interface IDApp {
    
    void init();
    
    /**
     * 获得区块链简称，用于区分不同链的游戏
     * @return 
     */
    String getBlockChainSymbol();
    
    /**
     * 返回游戏唯一id，默认使用包名
     * @return 
     */
    default String getId() {
	return this.getClass().getPackage().getName();
    }
    
    /**
     * 返回DApp名称
     * @return 
     */
    String getName();
    
    /**
     * 返回DApp描述
     * @return 
     */
    String getDesc();
    
    /**
     * 获得Dapp图标
     * @return 
     */
    ImageIcon getImageIcon();
    
    /**
     * 启动DApp(外部DApp可能涉及到拷贝文件等操作，故需启动线程执行)
     * @param param
     * @throws java.lang.Exception
     */
    void launch(String param) throws Exception;
    
    boolean isInternal();
}
