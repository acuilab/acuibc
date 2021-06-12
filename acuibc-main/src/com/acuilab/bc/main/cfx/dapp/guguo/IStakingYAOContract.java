package com.acuilab.bc.main.cfx.dapp.guguo;

import java.math.BigInteger;
import org.javatuples.Pair;

/**
 *
 * @author chia1
 */
public interface IStakingYAOContract {
    
    BigInteger yaoBalance(String address);
    
    BigInteger yaoCfxBalance(String address);
    
    BigInteger totalReleased();
    
    BigInteger pendingToken(String address, int pId);
    
    Pair<BigInteger[], BigInteger[]> pledgedERC1155(String address, int pId);
    
    String withdrawPoolAll(String privateKey) throws Exception;
}
