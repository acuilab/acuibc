package com.acuilab.bc.main.nft;

import java.awt.Image;
import java.io.IOException;
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
     */
    MetaData getMetaData(BigInteger tokenId) throws Exception;
    
    /**
     * 
     * @param from
     * @param to
     * @param id 
     */
    String safeTransferFrom(String privateKey, String from, String to, BigInteger tokenId, String data) throws Exception;
    
    /**
     * 
     * @param from
     * @param to
     * @param id 
     */
    default String safeTransferFrom(String privateKey, String from, String to, BigInteger tokenId) throws Exception {
	return safeTransferFrom(privateKey, from, to, tokenId, "*");
    }

}
