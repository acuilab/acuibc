package com.acuilab.bc.main.dapp;

import java.awt.Image;

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
     * 返回游戏名称
     */
    String getName();
    
    /**
     * 返回游戏描述
     * @return 
     */
    String getDesc();
    
    /**
     * 获得游戏图片，用于游戏列表展示，大小固定为178*178
     * @return 
     */
    Image getImage();
    
    /**
     * 启动DApp(可能涉及到拷贝文件等操作，故需启动线程执行)
     */
    void launch(String privateKey) throws Exception;
}
