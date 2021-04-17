package com.acuilab.bc.main.cfx;

import java.math.BigInteger;

/**
 * conflux链上的扩展功能
 */
public interface CFXExtend {
    
    String send(String privateKey, String from, BigInteger gas, String to, BigInteger value, BigInteger storageLimit, String data) throws Exception;

    String sign(String privateKey, String data) throws Exception;
}
