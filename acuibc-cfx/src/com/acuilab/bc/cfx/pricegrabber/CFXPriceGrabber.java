package com.acuilab.bc.cfx.pricegrabber;

import com.acuilab.bc.main.coin.IPriceGrabber;
import com.acuilab.bc.main.util.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 *
 * @author acuilab.com
 */
public class CFXPriceGrabber implements IPriceGrabber {
    
    public static final String MOONSWAP_MARKET_DATA_URL = "https://moonswap.fi/api/route/opt/moonswap/tickers";

    @Override
    public void grabPrice(Map<String, Double> map) throws Exception {
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
                
                Map<String, Double> lastPriceMap = Maps.newHashMap();

                MutableValueGraph<String, Integer> graph = ValueGraphBuilder.undirected().build();
                for (final JsonNode objNode : jsonNode) {
                    String baseCurrency = objNode.get("base_currency").asText();
                    String targetCurrency = objNode.get("target_currency").asText();
                    String lastPrice = objNode.get("last_price").asText();

                    graph.putEdgeValue(baseCurrency, targetCurrency, 1);
                    lastPriceMap.put(baseCurrency + "_" + targetCurrency, NumberUtils.toDouble(lastPrice));
                    lastPriceMap.put(targetCurrency + "_" + baseCurrency, 1/NumberUtils.toDouble(lastPrice));
                }

                System.out.println("weightedGraph=====================" + graph.toString());
                
                Map<String, NodeExtra> nodeExtras = doDijkstra(graph, "USDT");
                /**
                 * 输出起始节点到每个节点的最短路径以及路径的途径点信息
                 */
                Set<String> keys = nodeExtras.keySet();
                for (String node : keys) {
                    NodeExtra extra = nodeExtras.get(node);
                    if (extra.distance < Integer.MAX_VALUE) {
                        // 计算价格
                        String[] paths = StringUtils.split(extra.path, " -> ");
                        String startNode = "USDT";
                        double price = 1d;
                        for(int i=1; i<paths.length; i++) {
                            price *= lastPriceMap.get(startNode + "_" + paths[i]);
                            startNode = paths[i];
                        }
                        System.out.println(node + " -> USDT" + ": min: " + extra.distance + ", path: " + extra.path + ", price: " + (1/price));
                        
                        map.put(node, 1/price);
                    }
                }
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    private static Map<String, NodeExtra> doDijkstra(MutableValueGraph<String, Integer> graph, String startNode) {
        Set<String> nodes = graph.nodes();
        Map<String, NodeExtra> nodeExtras = Maps.newHashMapWithExpectedSize(nodes.size());
        /**
         * 初始化extra
         */
        for (String node : nodes) {
            NodeExtra extra = new NodeExtra();
            extra.nodeName = node;
            /*初始最短路径：存在边时，为边的权值；没有边时为无穷大*/
            final int value = graph.edgeValueOrDefault(startNode, node, Integer.MAX_VALUE);
            extra.distance = value;
            extra.visited = false;
            if (value < Double.MAX_VALUE) {
                extra.path = startNode + " -> " + node;
                extra.preNode = startNode;
            }
            nodeExtras.put(node, extra);
        }

        /**
         * 一开始可设置开始节点的最短路径为0
         */
        NodeExtra current = nodeExtras.get(startNode);
        current.distance = 0;
        current.visited = true;
        current.path = startNode;
        current.preNode = startNode;
        /*需要循环节点数遍*/
        for (String node : nodes) {
            NodeExtra minExtra = null;
            double min = Double.MAX_VALUE;
            /**
             * 找出起始点当前路径最短的节点
             */
            for (String notVisitedNode : nodes) {
                NodeExtra extra = nodeExtras.get(notVisitedNode);
                if (!extra.visited && extra.distance < min) {
                    min = extra.distance;
                    minExtra = extra;
                }
            }

            /**
             * 更新找到的最短路径节点的extra信息（获取的标志、路径信息）
             */
            if (minExtra != null) {
                minExtra.visited = true;
                minExtra.path = nodeExtras.get(minExtra.preNode).path + " -> " + minExtra.nodeName;
                current = minExtra;
            }

            /**
             * 并入新查找到的节点后，更新与其相关节点的最短路径中间结果
             * if (D[j] + arcs[j][k] < D[k]) D[k] = D[j] + arcs[j][k]
             */
            Set<String> successors = graph.successors(current.nodeName); //只需循环当前节点的后继列表即可
            for (String notVisitedNode : successors) {
                NodeExtra extra = nodeExtras.get(notVisitedNode);
                if (!extra.visited) {
                    final int value = current.distance + graph.edgeValueOrDefault(current.nodeName, notVisitedNode, 0);
                    if (value < extra.distance) {
                        extra.distance = value;
                        extra.preNode = current.nodeName;
                    }
                }
            }
        }
        
        return nodeExtras;
    }
    
//    private static <T> String format(Iterable<T> iterable) {
//        StringBuilder builder = new StringBuilder();
//        builder.append("{");
//        for (T obj : iterable) {
//            builder.append(obj).append(",");
//        }
//        if (builder.length()  > 1) {
//            builder.deleteCharAt(builder.length() - 1);
//        }
//        builder.append("}");
//        return builder.toString();
//    }

    @Override
    public String getBlockChainSymbol() {
        return Constants.CFX_BLOCKCHAIN_SYMBAL;
    }

    private static class NodeExtra {
        public String nodeName; //当前的节点名称
        public int distance; //开始点到当前节点的最短路径
        public boolean visited; //当前节点是否已经求的最短路径
        public String preNode; //前一个节点名称
        public String path; //路径的所有途径点
    }
}
