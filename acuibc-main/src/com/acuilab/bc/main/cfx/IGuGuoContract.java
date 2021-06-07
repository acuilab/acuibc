package com.acuilab.bc.main.cfx;

import com.acuilab.bc.main.nft.MetaData;
import java.math.BigInteger;

/**
 *
 * @author chia1
 */
public interface IGuGuoContract {
    
    public static final int KAOZI_PID = 0;
    public static final int MOON_PID = 1;
    public static final int FLUX_PID = 2;
    public static final int GUGUO_PID = 3;
    
    BigInteger xiangBalance(String address);
    
    BigInteger yaoBalance(String address);
    
    BigInteger[] pledgedERC1155(String address, int pid);
    
    MetaData getMetaData(int pId, BigInteger tokenId) throws Exception;
}
