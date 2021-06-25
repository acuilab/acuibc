package com.acuilab.bc.main.cfx.dapp.guguo;

import java.math.BigInteger;

/**
 *
 * @author chia1
 */
public interface IGuGuoNFT {
    // 抽卡
    String pickCards(String privateKey, BigInteger poorId) throws Exception;
    
    /**
     * 
     * @param privateKey
     * @param poolId
     * @param delay             延时毫秒数
     * @param needWithdrawXiang 执行前收一次xiang
     * @param callback
     * @throws Exception 
     */
    void batchPickCards(String privateKey, BigInteger poolId, long delay, boolean needWithdrawXiang, BatchPickCardsCallback callback) throws Exception;
    
    BigInteger getPoolCounts();
    
    interface BatchPickCardsCallback {
	void withdrawXiang(String address, BigInteger nonce, String hash);
        
        void pickCards(String address, BigInteger nonce, String hash);
        
        void exceptionThrowed(String address, BigInteger nonce, String message);
    }
}
