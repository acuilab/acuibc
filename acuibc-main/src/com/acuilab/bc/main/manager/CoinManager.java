package com.acuilab.bc.main.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import org.openide.util.Lookup;
import com.acuilab.bc.main.coin.ICoin;
import java.util.Map;

/**
 *
 * @author admin
 */
public class CoinManager {
    private static CoinManager instance;
    // blockchainsymbol<=>(symbol <=> coin)
    private final Map<String, Map<String, ICoin>> map;
    
    public static CoinManager getDefault() {
        if (instance == null) {
            instance = new CoinManager();
        }
        return instance;
    }
    
    private CoinManager() {
        map = Maps.newHashMap();
        Collection<? extends ICoin> list = Lookup.getDefault().lookupAll(ICoin.class);
        for (ICoin c : list) {
            c.init();
	    
	    Map tmp = map.get(c.getBlockChainSymbol());
	    if(tmp == null) {
		tmp = Maps.newHashMap();
		map.put(c.getBlockChainSymbol(), tmp);
	    }
	    
            tmp.put(c.getSymbol(), c);
        }
    }
    
    public ICoin getCoin(String blockChainSymbol, String symbol) {
	Map<String, ICoin> tmp = map.get(blockChainSymbol);
	if(tmp != null) {
	    return tmp.get(symbol);
	}
	
        return null;
    }
    
    public ICoin getBaseCoin(String blockChainSymbol) {
	Map<String, ICoin> tmp = map.get(blockChainSymbol);
	if(tmp != null) {
	    // 遍历tmp，获得base coin
	    for(ICoin coin : tmp.values()) {
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
	Map<String, ICoin> tmp = map.get(blockChainSymbol);
	if(tmp != null) {
	    ICoin baseCoin = null;
	    // 遍历tmp，获得base coin
	    for(ICoin coin : tmp.values()) {
                if(!coin.isBaseCoin()) {
                    list.add(coin);
                } else {
                    baseCoin = coin;
                }
	    }
	    if(baseCoin != null) {
		list.add(0, baseCoin);
	    }
	}
	
        return list;
    }
}
