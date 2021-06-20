package com.acuilab.bc.main.manager;

import com.acuilab.bc.main.coin.IPriceGrabber;
import com.acuilab.bc.main.util.Constants;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import org.openide.util.Lookup;

/**
 *
 * @author acuilab.com
 */
public class PriceManager {
    private static final Logger LOG = Logger.getLogger(PriceManager.class.getName());
    private static PriceManager instance; 
    
    private static ScheduledExecutorService service;
    private static final int REFRESH_INTERVAL = 60 * 1000;      // 一分钟刷新一次
    private static final int TERMINATE_TIMEOUT = 6 * 1000;
    
    // blockChainSymbol, coinSymbol, price
    private final Map<String, Map<String, Double>> map = Maps.newHashMap();
    
    public static PriceManager getDefault() {
        if (instance == null) {
            instance = new PriceManager();
        }
        return instance;
    }
    
    private PriceManager() {
        map.put(Constants.CFX_BLOCKCHAIN_SYMBAL, Maps.newConcurrentMap());
    }
    
    public void start() {
        service = Executors.newScheduledThreadPool(1);
        
        service.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // conflux
                Collection<? extends IPriceGrabber> list = Lookup.getDefault().lookupAll(IPriceGrabber.class);
                for(IPriceGrabber grabber : list) {
                    try {
                        String blockChainSymbol = grabber.getBlockChainSymbol();
                        grabber.grabPrice(map.get(blockChainSymbol));
                    } catch(Exception e) {
                        LOG.warning("IPriceGrabber.grabPrice exception occured!");
                    }
                }
            }
            
        }, 0l, REFRESH_INTERVAL, TimeUnit.MILLISECONDS);
    }
    
    public void stop() throws InterruptedException {
        
        service.awaitTermination(TERMINATE_TIMEOUT, TimeUnit.MILLISECONDS);
    }
    
    public Double getPrice(String blockChainSymbol, String coinSymbol) {
        Map<String, Double> cMap = map.get(blockChainSymbol);
        if(cMap != null) {
            return cMap.get(coinSymbol);
        }
        
        return null;
    }
}
