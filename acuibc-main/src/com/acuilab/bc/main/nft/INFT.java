package com.acuilab.bc.main.nft;

import java.awt.Image;
import java.math.BigInteger;
import javax.swing.Icon;

/**
 *
 * @author admin
 */
public interface INFT {
    public void init();
    
    // 名称
    public String getName();
    
    // 简称（外键）
    public String getSymbol();
    
    // 合约地址
    public String getContractAddress();
    
    // 合约地址
    public String getWebsite();
    
    
    /**
     * 获得区块链简称
     * @return 
     */
    public String getBlockChainSymbol();
    
    /**
     * 获得图标
     * @param size
     * @return 
     */
    Icon getIcon(int size);
    
    Image getIconImage(int size);
    
    /**
     * 获得某个地址的tokenId列表
     * @param address
     * @return 
     */
    BigInteger[] tokensOf(String address);
    
    /**
     * 
     * @param tokenId
     * @return	  
     * @throws java.lang.Exception	  
     */
    MetaData getMetaData(BigInteger tokenId) throws Exception;
    
    /**
     * 
     * @param privateKey
     * @param from
     * @param to 
     * @param tokenId 
     * @param data 
     * @param gas 
     * @return  
     * @throws java.lang.Exception 
     */
    String safeTransferFrom(String privateKey, String from, String to, BigInteger tokenId, String data, BigInteger gas) throws Exception;
    
    /**
     * 
     * @param privateKey
     * @param from
     * @param to 
     * @param tokenId 
     * @param gas 
     * @return  
     * @throws java.lang.Exception  
     */
    default String safeTransferFrom(String privateKey, String from, String to, BigInteger tokenId, BigInteger gas) throws Exception {
	return safeTransferFrom(privateKey, from, to, tokenId, "", gas);
    }

    /**
     * 矿工费最小值
     * @return 
     */
    int gasMin();
    
    /**
     * 矿工费最大值
     * @return 
     */
    int gasMax();
    
    /**
     * 矿工费初值
     * @return 
     */
    int gasDefault();
    
    /**
     * 矿工费描述：例如1drip
     * @param gas
     * @return 
     */
    String gasDesc(int gas);
    
    int gasLimit();
}
