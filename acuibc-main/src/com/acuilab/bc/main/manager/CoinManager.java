package com.acuilab.bc.main.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.openide.util.Lookup;
import com.acuilab.bc.main.coin.ICoin;
import java.util.Map;

/**
 *
 * @author admin
 */
public class CoinManager {
    private static CoinManager instance;
    // name <=> coin
    private LinkedHashMap<String, ICoin> map;
    // symbol <=> coin
    private Map<String, ICoin> map2;
    
    public static CoinManager getDefault() {
        if (instance == null) {
            instance = new CoinManager();
        }
        return instance;
    }
    
    private CoinManager() {
        map = Maps.newLinkedHashMap();
        map2 = Maps.newHashMap();
        Collection<? extends ICoin> list = Lookup.getDefault().lookupAll(ICoin.class);
        for (ICoin c : list) {
            c.init();
            map.put(c.getName(), c);
            map2.put(c.getSymbol(), c);
        }
        sort();
    }
    
    private void sort() {
        List<String> mapKeys = Lists.newArrayList(map.keySet());
        Collections.sort(mapKeys);

        LinkedHashMap<String, ICoin> someMap = Maps.newLinkedHashMap();
        for (int i = 0; i < mapKeys.size(); i++) {
            someMap.put(mapKeys.get(i), map.get(mapKeys.get(i)));
        }
        map = someMap;
    }
    
    public ICoin getCoin(String key) {
        return map.get(key);
    }
    
    public ICoin getBaseCoin(String blockChainSymbol) {
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            ICoin coin = map.get(it.next());
            if(StringUtils.equals(coin.getBlockChainSymbol(), blockChainSymbol)) {
                if(coin.isBaseCoin()) {
                    // 主网币只能有一个
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
    public List<ICoin> getCoinList(String blockChainSymbol) {
        List<ICoin> list = Lists.newArrayList();
        Iterator<String> it = map.keySet().iterator();
        ICoin baseCoin = null;
        while (it.hasNext()) {
            ICoin coin = map.get(it.next());
            if(StringUtils.equals(coin.getBlockChainSymbol(), blockChainSymbol)) {
                if(!coin.isBaseCoin()) {
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
