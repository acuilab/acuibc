package com.acuilab.bc.main.cfx.dapp.guguo;

import java.math.BigInteger;

/**
 *
 * @author chia1
 */
public interface IStakingXIANGContract {
    
    BigInteger xiangBalance(String address);
    
    // 获得池内质押总量
    BigInteger poolPledged(BigInteger pId);
    
    // 我的质押
    BigInteger pledgedAmount(String address, BigInteger pId);
    
    // 当前收益
    BigInteger pendingToken(String address, BigInteger pId);
    
    // 提取
    String withdrawPool(String privateKey, BigInteger pId) throws Exception;
    
    // 提取
    String withdrawPoolAll(String privateKey) throws Exception;
    
    // 质押yao
    String depositERC20(String privateKey, BigInteger amount, BigInteger pId) throws Exception;
    
    // 取出yao
    String withdrawERC20(String privateKey, BigInteger amount, BigInteger pId) throws Exception;
}
