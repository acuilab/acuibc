package com.acuilab.bc.main.wallet;

/**
 * 代币
 * @author admin
 */
public interface TokenCoin extends Coin {
    /**
     * 获得合约地址
     * @return 
     */
    String getContractAddress();
}
