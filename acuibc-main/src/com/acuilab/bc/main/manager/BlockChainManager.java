package com.acuilab.bc.main.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.openide.util.Lookup;
import com.acuilab.bc.main.BlockChain;

/**
 *
 * @author admin
 */
public class BlockChainManager {
    private static BlockChainManager instance;
    private LinkedHashMap<String, BlockChain> map;
    
    public static BlockChainManager getDefault() {
        if (instance == null) {
            instance = new BlockChainManager();
        }
        return instance;
    }
    
    private BlockChainManager() {
        map = Maps.newLinkedHashMap();
        Collection<? extends BlockChain> list = Lookup.getDefault().lookupAll(BlockChain.class);
        for (BlockChain bc : list) {
            bc.setDefaultNode();
            map.put(bc.getSymbol(), bc);
	    System.out.println("------------------------" + bc.getSymbol());
        }
        sort();
    }
    
    private void sort() {
        List<String> mapKeys = Lists.newArrayList(map.keySet());
        Collections.sort(mapKeys);

        LinkedHashMap<String, BlockChain> someMap = Maps.newLinkedHashMap();
        for (int i = 0; i < mapKeys.size(); i++) {
            someMap.put(mapKeys.get(i), map.get(mapKeys.get(i)));
        }
        map = someMap;
    }
    
    public void close() {
        Iterator<BlockChain> it = map.values().iterator();
        while (it.hasNext()) {
            it.next().close();
        }
    }
    
    
    public BlockChain getBlockChain(String key) {
        return map.get(key);
    }
    
    public List<BlockChain> geBlockChainList() {
        List<BlockChain> list = Lists.newArrayList();
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            list.add(map.get(it.next()));
        }
        return list;
    }

    public String[] getBlockChainSymbols() {
        List<String> list = Lists.newArrayList(map.keySet());
        Collections.sort(list);
        return list.toArray(new String[list.size()]);
    }
    
    public String[] getBlockChainNames() {
        List<String> list = Lists.newArrayList();
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            BlockChain bc = map.get(it.next());
            list.add(bc.getName());
        }
        Collections.sort(list);
        return list.toArray(new String[list.size()]);
    }
}
