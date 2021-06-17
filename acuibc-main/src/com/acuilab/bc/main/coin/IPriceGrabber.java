package com.acuilab.bc.main.coin;

import java.math.BigDecimal;
import java.util.concurrent.ConcurrentMap;

/**
 *
 * @author acuilab.com
 */
public interface IPriceGrabber {
    String getBlockChainSymbol();
    
    void grabPrice(ConcurrentMap<String, BigDecimal> map) throws Exception;
}
