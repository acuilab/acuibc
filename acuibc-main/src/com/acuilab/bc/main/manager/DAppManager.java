package com.acuilab.bc.main.manager;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.openide.util.Lookup;
import java.util.Comparator;
import java.util.stream.Collectors;
import com.acuilab.bc.main.dapp.IDApp;

/**
 *
 * @author admin
 */
public class DAppManager {
    private static DAppManager instance;
    private LinkedHashMap<String, IDApp> map;
    
    public static DAppManager getDefault() {
        if (instance == null) {
            instance = new DAppManager();
        }
        return instance;
    }
    
    private DAppManager() {
        map = Maps.newLinkedHashMap();
        Collection<? extends IDApp> list = Lookup.getDefault().lookupAll(IDApp.class);
        for (IDApp dapp : list) {
            dapp.init();
            map.put(dapp.getId(), dapp);
        }
        sort();
    }
    
    private void sort() {
	// 按名称排序
        List<IDApp> list = Lists.newArrayList(map.values());
	List<IDApp> listSorted = list.stream().sorted(Comparator.comparing(IDApp::getName)).collect(Collectors.toList());
	
        LinkedHashMap<String, IDApp> someMap = Maps.newLinkedHashMap();
        for (IDApp dapp : listSorted) {
            someMap.put(dapp.getId(), dapp);
        }
        map = someMap;
    }
    
    public IDApp getDApp(String key) {
        return map.get(key);
    }
    
    /**
     * 获得DApp列表
     * @return 
     */
    public List<IDApp> getDAppList() {
        List<IDApp> list = Lists.newArrayList();
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
	    list.add(map.get(it.next()));
        }
        
        return list;
    }
    
    /**
     * 获得某个区块链的DApp列表
     * @param blockChainSymbol
     * @return 
     */
    public List<IDApp> getDAppList(String blockChainSymbol) {
        List<IDApp> list = Lists.newArrayList();
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            IDApp dapp = map.get(it.next());
            if(StringUtils.equals(dapp.getBlockChainSymbol(), blockChainSymbol)) {
		list.add(dapp);
            }
        }
        
        return list;
    }
    
    /**
     * 获得某个区块链的DApp列表
     * @return 
     */
    public List<IDApp> getInternalDAppList() {
        List<IDApp> list = Lists.newArrayList();
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            IDApp dapp = map.get(it.next());
            if(dapp.isInternal()) {
		list.add(dapp);
            }
        }
        
        return list;
    }
    
    /**
     * 获得某个区块链的DApp列表
     * @param blockChainSymbol
     * @return 
     */
    public List<IDApp> getExternalDAppList(String blockChainSymbol) {
        List<IDApp> list = Lists.newArrayList();
        Iterator<String> it = map.keySet().iterator();
        while (it.hasNext()) {
            IDApp dapp = map.get(it.next());
            if(StringUtils.equals(dapp.getBlockChainSymbol(), blockChainSymbol) && !dapp.isInternal()) {
		list.add(dapp);
            }
        }
        
        return list;
    }
}
