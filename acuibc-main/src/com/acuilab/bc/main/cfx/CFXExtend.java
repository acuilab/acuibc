package com.acuilab.bc.main.cfx;

import java.math.BigInteger;

/**
 * conflux链上的扩展功能
 */
public interface CFXExtend {
    
    String send(String privateKey, String from, BigInteger gas, String to, BigInteger value, BigInteger storageLimit, String data) throws Exception;

    String sign(String privateKey, String data) throws Exception;
    
    /**
     * 将旧地址转换为新地址(如果是新地址，则原样返回)
     * @param address
     * @return 
     */
    String convertAddress(String address);
    
    void generateJavaClass(String rootDirectory) throws Exception;
}
