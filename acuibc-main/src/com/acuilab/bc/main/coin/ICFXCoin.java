package com.acuilab.bc.main.coin;

import java.math.BigInteger;

/**
 *
 * @author admin
 */
public interface ICFXCoin extends ICoin {
    
    public String deposit(String privateKey, BigInteger value) throws Exception;
    
    public String withdraw(String privateKey, BigInteger value) throws Exception;
    
    public BigInteger stakingBalanceOf(String address);
}
