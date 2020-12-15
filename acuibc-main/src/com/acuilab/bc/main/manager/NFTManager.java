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
import com.acuilab.bc.main.nft.INFT;

/**
 *
 * @author admin
 */
public class NFTManager {
    private static NFTManager instance;
    private LinkedHashMap<String, INFT> map;
    
    public static NFTManager getDefault() {
        if (instance == null) {
            instance = new NFTManager();
        }
        return instance;
    }
    
    private NFTManager() {
        map = Maps.newLinkedHashMap();
        Collection<? extends INFT> list = Lookup.getDefault().lookupAll(INFT.class);
        for (INFT nft : list) {
            nft.init();
            map.put(nft.getName(), nft);
        }
        sort();
    }
    
    private void sort() {
        List<String> mapKeys = Lists.newArrayList(map.keySet());
        Collections.sort(mapKeys);

        LinkedHashMap<String, INFT> someMap = Maps.newLinkedHashMap();
        for (int i = 0; i < mapKeys.size(); i++) {
            someMap.put(mapKeys.get(i), map.get(mapKeys.get(i)));
        }
        map = someMap;
    }
    
    public INFT getNFT(String key) {
        return map.get(key);
    }
    
    /**
     * 获得某个区块链的NFT列表
     * @param blockChainSymbol
     * @return 
     */
    public List<INFT> getNFTList(String blockChainSymbol) {
        List<INFT> list = Lists.newArrayList();
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            INFT nft = map.get(it.next());
            if(StringUtils.equals(nft.getBlockChainSymbol(), blockChainSymbol)) {
		list.add(nft);
            }
        }
        
        return list;
    }
}
