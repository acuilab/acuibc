package com.acuilab.bc.main.dapp;

import javax.swing.ImageIcon;

/**
 * dapp应用接口，显示在DAppStore窗口中，现在该接口大致分为3类：
 *  1、独立应用程序，目前仅一种，实现为FatJarDApp，例如基于LibGDX的游戏
 *  2、浏览器应用，各个链分开，目前仅支持conflux，实现为CfxBrowserDApp（后续可支持以太、bsc等）
 *      兼容是双向的，浏览器要适应dapp，同样dapp也要适应浏览器，有些dapp应用需要修改才能适应浏览器，如果实在不行，或者合约开源的话，可以采用第三种方式直接访问合约，否则只能放弃支持
 *  3、其他，如批量转账
 * 对于前两种，一般需要一个向导，先选择钱包，再输入密码并校验，通过后启动；对于后一种则比较灵活，有可能都不需要指定特定的钱包
 * 前两种通常对于同一个应用通常可启动多个实例；后一种则可视具体情况，例如批量转账可在统一界面选择多个钱包，故同时启动一个实例较为合适
 * 第一种特别独立，在自己的模块实现；后两种目前直接在main模块实现
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
	return this.getClass().getName();
    }
    
    /**
     * 返回DApp名称
     * @return 
     */
    String getName();
    
    /**
     * 返回DApp类型
     * @return 
     */
    String getType();
    
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
     * @throws java.lang.Exception
     */
    void launch(String address, String privateKey) throws Exception;
    
    /**
     * 是否需要指定钱包
     *  启动应用前先弹出选择钱包向导
     * @return 
     */
    default boolean needWalletSpecified() {
        return true;
    }
}
