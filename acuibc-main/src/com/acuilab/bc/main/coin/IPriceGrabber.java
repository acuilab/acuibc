package com.acuilab.bc.main.coin;

import java.util.Map;

/**
 *
 * @author acuilab.com
 */
public interface IPriceGrabber {
    String getBlockChainSymbol();
    
    void grabPrice(Map<String, Double> map) throws Exception;
}
