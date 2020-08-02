package com.acuilab.bc.main;

import com.acuilab.bc.main.wallet.Wallet;
import java.util.List;
import javax.swing.Icon;

/**
 * 通过BlockChain接口屏蔽不同区块链之间的差别
 * 
 * @author admin
 */
public interface BlockChain {
    
    // TODO: 判断区块链是否连接，如果不连接（本地网络问题），则对应的钱包无法操作；更换节点
    // JSON-RPC是一个无状态且轻量级的RPC协议，其传输内容以JSON方式
    
    /**
     * 设置默认节点 
     */
    void setDefaultNode();
    
    /**
     * 设置节点
     * @param node 
     */
    void setNode(String node);
    
    /**
     * 获得默认节点
     * @return 
     */
    String getDefaultNode();
    
    /**
     * 
     * @return 
     */
    String getBIP44Path();
    
    /**
     * 关闭
     */
    void close();
    
    /**
     * 返回区块链的名称：例如Conflux
     * @return 
     */
    String getName();
    
    /**
     * 返回区块链简称：例如CFX
     * @return 
     */
    String getSymbol();
    
    /**
     * 获得图标
     * @param size
     * @return 
     */
    Icon getIcon(int size);
    
    /**
     * 创建钱包
     * @param name
     * @param pwd
     * @param mnemonicWords
     * @return 
     */
    Wallet createWalletByMnemonic(String name, String pwd, List<String> mnemonicWords);
    
    /**
     * 根据私钥导入钱包
     * @param name
     * @param pwd
     * @param privateKey
     * @return 
     */
    Wallet importWalletByPrivateKey(String name, String pwd, String privateKey);
    
    /**
     * 根据助记词导入钱包
     * @param name
     * @param pwd
     * @param mnemonic
     * @return 
     */
    Wallet importWalletByMnemonic(String name, String pwd, String mnemonic);
    
    /**
     * 判断是否是有效地址
     * @param address
     * @return 
     */
    boolean isValidAddress(String address);
}
