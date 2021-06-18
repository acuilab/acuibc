package com.acuilab.bc.cfx.pricegrabber;

import com.acuilab.bc.main.coin.IPriceGrabber;
import com.acuilab.bc.main.util.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tree.TreeNode;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;

/**
 *
 * @author acuilab.com
 */
public class CFXPriceGrabber implements IPriceGrabber {
    
    public static final String MOONSWAP_MARKET_DATA_URL = "https://moonswap.fi/api/route/opt/moonswap/tickers";
    private static final int MAX_TREE_LEVEL = 10;

    @Override
    public void grabPrice(ConcurrentMap<String, BigDecimal> map) throws Exception {
        try {
            System.out.println("CFXPriceGrabber grabPrice .......................................................");
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();
            final okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(MOONSWAP_MARKET_DATA_URL)
                    .build();
            final Call call = okHttpClient.newCall(request);
            okhttp3.Response response = call.execute();             // java.net.SocketTimeoutException
            ResponseBody body = response.body();
            if(body != null) {
                // ①解析json
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(body.string());

                List<Data> list = Lists.newArrayList();
                for (final JsonNode objNode : jsonNode) {
                    System.out.println("base_currency=" + objNode.get("base_currency").asText());

                    Data data = new Data(objNode.get("base_currency").asText(), 
                            objNode.get("target_currency").asText(), 
                            objNode.get("last_price").asText());
                    list.add(data);
                }

                // ②根据list构造树
                List<Data> tmp = Lists.newArrayList();
                TreeNode<Data> rootNode = new TreeNode<>(new Data("USDT", "USDT", "1"));
                while(!list.isEmpty()) {
                    System.out.println("list.size()====================" + list.size());
                    tmp.clear();
                    for(Data data : list) {
                        Comparable<Data> searchCriteria = new Comparable<Data>() {
                                @Override
                                public int compareTo(Data treeData) {
                                        if (treeData == null || "USDT".equals(treeData.getBaseCurrency()))
                                                return 1;
                                        System.out.println("treeData.getBaseCurrency()=====================" + treeData.getBaseCurrency() + ", data.getTargetCurrency()=====================" + data.getTargetCurrency());
                                        boolean nodeOk = StringUtils.equals(treeData.getBaseCurrency(), data.getTargetCurrency());
                                        return nodeOk ? 0 : 1;
                                }
                        };
                        
                        TreeNode<Data> found = rootNode.findTreeNode(searchCriteria);
                        if(found != null) {
                            System.out.println("found===========" + found);
                            found.addChild(data);
                            tmp.add(data);
                        }
                    }
                    
                    list.removeAll(tmp);
                    
                    
//                    System.out.println("list.size()=====" + list.size());
//                    tmp.clear();
//                    for (TreeNode<Data> node : rootNode) {
//                        for(Data data : list) {
//                            if(StringUtils.equals(node.data.getBaseCurrency(), data.getTargetCurrency())) {
//                                
//                                node.
//                                tmp.add(new Pair<>(node, data));
//                            }
//                        }
//                    }
//
//                    for(Pair<TreeNode<Data>, Data> pair : tmp) {
//                        TreeNode<Data> node = pair.getValue0();
//                        Data data = pair.getValue1();
//                        node.addChild(data);
//                        list.remove(data);
//                    }

                    if(rootNode.getLevel() >= MAX_TREE_LEVEL) {
                        break;
                    }
                }

                // ③遍历树更新map
                for (TreeNode<Data> node : rootNode) {
                    String baseCurrency = node.data.getBaseCurrency();
                    BigDecimal price = new BigDecimal(node.data.getLastPrice());
                    while(!node.parent.isRoot()) {
                        price.multiply(new BigDecimal(node.parent.data.getLastPrice()));
                        node = node.parent;
                    }
                    map.put(baseCurrency, price);
                }

                // 
                for (Map.Entry<String, BigDecimal> entry : map.entrySet()) {
                 String key = entry.getKey();
                 String value = entry.getValue().toString();
                 System.out.println("key=" + key + " value=" + value);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getBlockChainSymbol() {
        return Constants.CFX_BLOCKCHAIN_SYMBAL;
    }

    class Data {
        private final String baseCurrency;
        private final String targetCurrency;
        private final String lastPrice;
        
        private final String key;
        
        public Data(String baseCurrency, String targetCurrency, String lastPrice) {
            this.baseCurrency = baseCurrency;
            this.targetCurrency = targetCurrency;
            this.lastPrice = lastPrice;
            
            this.key = baseCurrency + "_" + targetCurrency;
        }

        public String getBaseCurrency() {
            return baseCurrency;
        }

        public String getTargetCurrency() {
            return targetCurrency;
        }

        public String getLastPrice() {
            return lastPrice;
        }
        
        public String getKey() {
            return key;
        }

        @Override
        public String toString() {
            return "Data{" + "baseCurrency=" + baseCurrency + ", targetCurrency=" + targetCurrency + ", lastPrice=" + lastPrice + '}';
        }
    }
}
