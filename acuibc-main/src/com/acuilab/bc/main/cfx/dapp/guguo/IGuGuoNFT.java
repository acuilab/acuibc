package com.acuilab.bc.main.cfx.dapp.guguo;

import java.math.BigInteger;

/**
 *
 * @author chia1
 */
public interface IGuGuoNFT {
    // 抽卡
    String pickCards(String privateKey, BigInteger poorId) throws Exception;
}
