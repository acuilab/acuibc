package com.acuilab.bc.main.cfx;

import java.math.BigInteger;

/**
 *
 * @author chia1
 */
public interface IGuGuoContract {
    
    BigInteger xiangBalance(String address);
    
    BigInteger yaoBalance(String address);
}
