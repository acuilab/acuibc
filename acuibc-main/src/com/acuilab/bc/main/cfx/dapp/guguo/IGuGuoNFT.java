package com.acuilab.bc.main.cfx.dapp.guguo;

import java.math.BigInteger;

/**
 *
 * @author chia1
 */
public interface IGuGuoNFT {
    // 抽卡
    String pickCards(String privateKey, BigInteger poorId) throws Exception;
    
    // 不停抽卡
    void pickCards2(String privateKey, BigInteger poorId, boolean needWithdrawXiang) throws Exception;
}
