package com.acuilab.bc.main.manager;

import com.acuilab.bc.main.wallet.Coin;
import com.acuilab.bc.main.wallet.Coin.Type;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.openide.util.Lookup;

/**
 *
 * @author admin
 */
public class CoinManager {
    private static CoinManager instance;
    private LinkedHashMap<String, Coin> map;
    
    public static CoinManager getDefault() {
        if (instance == null) {
            instance = new CoinManager();
        }
        return instance;
    }
    
    private CoinManager() {
        map = Maps.newLinkedHashMap();
        Collection<? extends Coin> list = Lookup.getDefault().lookupAll(Coin.class);
        for (Coin c : list) {
            c.init();
            map.put(c.getName(), c);
        }
        sort();
    }
    
    private void sort() {
        List<String> mapKeys = Lists.newArrayList(map.keySet());
        Collections.sort(mapKeys);

        LinkedHashMap<String, Coin> someMap = Maps.newLinkedHashMap();
        for (int i = 0; i < mapKeys.size(); i++) {
            someMap.put(mapKeys.get(i), map.get(mapKeys.get(i)));
        }
        map = someMap;
    }
    
    public Coin getCoin(String key) {
        return map.get(key);
    }
    
    public Coin getBaseCoin(String blockChainSymbol) {
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            Coin coin = map.get(it.next());
            if(StringUtils.equals(coin.getBlockChainSymbol(), blockChainSymbol)) {
                if(coin.getType() == Type.BASE) {
                    return coin;
                }
            }
        }
        
        return null;
    }
    
    /**
     * 获得某个区块链的币列表，且主网币排在第一位
     * @param blockChainSymbol
     * @return 
     */
    public List<Coin> getCoinList(String blockChainSymbol) {
        List<Coin> list = Lists.newArrayList();
        Iterator<String> it = map.keySet().iterator();
        Coin baseCoin = null;
        while (it.hasNext()) {
            Coin coin = map.get(it.next());
            if(StringUtils.equals(coin.getBlockChainSymbol(), blockChainSymbol)) {
                if(coin.getType() == Type.TOKEN) {
                    list.add(coin);
                } else {
                    baseCoin = coin;
                }
            }
        }
        if(baseCoin != null) {
            list.add(0, baseCoin);
        }
        
        return list;
    }
//    
//    public List<String> getCoinNames(String symbol) {
//        List<String> list = Lists.newArrayList(map.keySet());
//        Collections.sort(list);
//        return list;
//    }
    
}
