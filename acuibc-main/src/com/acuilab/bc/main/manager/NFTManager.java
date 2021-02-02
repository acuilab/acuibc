package com.acuilab.bc.main.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.List;
import org.openide.util.Lookup;
import com.acuilab.bc.main.nft.INFT;
import java.util.Map;

/**
 *
 * @author admin
 */
public class NFTManager {
    private static NFTManager instance;
    // blockchainsymbol<=>(symbol <=> nft)
    private final Map<String, Map<String, INFT>> map;
    
    public static NFTManager getDefault() {
        if (instance == null) {
            instance = new NFTManager();
        }
        return instance;
    }
    
    private NFTManager() {
        map = Maps.newHashMap();
        Collection<? extends INFT> list = Lookup.getDefault().lookupAll(INFT.class);
        for (INFT nft : list) {
            nft.init();
	    
	    Map tmp = map.get(nft.getBlockChainSymbol());
	    if(tmp == null) {
		tmp = Maps.newHashMap();
		map.put(nft.getBlockChainSymbol(), tmp);
	    }
	    
            tmp.put(nft.getSymbol(), nft);
        }
    }
    
    public INFT getNFT(String blockChainSymbol, String symbol) {
	Map<String, INFT> tmp = map.get(blockChainSymbol);
	if(tmp != null) {
	    return tmp.get(symbol);
	}
	
        return null;
    }
    
    /**
     * 获得某个区块链的NFT列表
     * @param blockChainSymbol
     * @return 
     */
    public List<INFT> getNFTList(String blockChainSymbol) {
        List<INFT> list = Lists.newArrayList();
	Map<String, INFT> tmp = map.get(blockChainSymbol);
	if(tmp != null) {
	    // 遍历tmp
	    for(INFT nft : tmp.values()) {
		list.add(nft);
	    }
	}
	
        return list;
    }
}
