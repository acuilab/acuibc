package com.acuilab.bc.main.cfx.dapp.dan;

import java.math.BigInteger;
import org.javatuples.Octet;

/**
 *
 * @author chia1
 */
public interface IStakingDanContract {
    
    Octet<BigInteger, BigInteger, BigInteger, Boolean, String[], BigInteger[], BigInteger[], BigInteger[]> stakingDetail(String account);
}
