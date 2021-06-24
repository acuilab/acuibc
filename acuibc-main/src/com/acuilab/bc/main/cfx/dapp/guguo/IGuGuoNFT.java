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
     * @throws Exception 
     */
    void pickCards2(String privateKey, BigInteger poolId, long delay, boolean needWithdrawXiang) throws Exception;
}
