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

/**
 *
 * @author acuilab.com
 */
public class CFXPriceGrabber implements IPriceGrabber {
    
    public static final String MOONSWAP_MARKET_DATA_URL = "https://moonswap.fi/api/route/opt/moonswap/tickers";

    @Override
    public void grabPrice(ConcurrentMap<String, BigDecimal> map) throws Exception {
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
            System.out.println("list.size()====================" + list.size());
            
            // ②根据list构造树
            TreeNode<Data> rootNode = new TreeNode<>(new Data("USDT", "USDT", "1"));
            Set<String> set = Sets.newHashSet();
            int nodeNumber = 0;
            while(nodeNumber<list.size()) {
                for(int i=0; i<list.size(); i++) {
                    System.out.println("i==============================" + i);
                    Data data = list.get(i);
                    
                    System.out.println(data);

                    // 遍历树
                    try {
                        for (TreeNode<Data> node : rootNode) {
                            System.out.println("node.data.getBaseCurrency==============================" + node.data.getBaseCurrency());
                            if(node.isLeaf() && !set.contains(data.getKey()) && StringUtils.equals(node.data.getBaseCurrency(), data.getTargetCurrency())) {
                                node.addChild(data);

                                System.out.println("nodeNumber===" + nodeNumber);
                                nodeNumber++;
                                set.add(data.getKey());
                            }
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }

                }
            }
            System.out.println("nodeNumber = " + nodeNumber);

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
             String key = entry.getKey().toString();
             String value = entry.getValue().toString();
             System.out.println("key=" + key + " value=" + value);
            }
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
