package com.acuilab.bc.main.cfx.dapp.guguo;

import java.math.BigInteger;

/**
 *
 * @author chia1
 */
public interface IStakingXIANGContract {
    
    BigInteger xiangBalance(String address);
    
    // 获得池内质押总量爻
    BigInteger poolPledged();
    
    // 我的质押
    BigInteger pledgedAmount(String address);
    
    // 当前收益
    BigInteger pendingToken(String address);
    
    // 提取
    String withdrawPoolAll(String privateKey) throws Exception;
}
