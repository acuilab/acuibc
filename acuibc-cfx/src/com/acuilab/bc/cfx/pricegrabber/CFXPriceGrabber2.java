package com.acuilab.bc.cfx.pricegrabber;

import com.acuilab.bc.main.coin.IPriceGrabber;
import com.acuilab.bc.main.util.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.math.NumberUtils;

/**
 *
 * @author acuilab.com
 */
public class CFXPriceGrabber2 implements IPriceGrabber {
    
    public static final String MOONSWAP_MARKET_DATA_URL = "https://moonswap.fi/api/route/opt/swap/main/token-price";
    
    @Override
    public void grabPrice(Map<String, Double> map) throws Exception {
        try {
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
                JsonNode root = mapper.readTree(body.string());
                JsonNode data = root.get("data");
                
                for (final JsonNode objNode : data) {
                    String contractAddress = objNode.get("contract_address").asText();
                    String priceUsd = objNode.get("price_usd").asText();
                    
                    String symbol = Constants.CFX_ADDRESS_SYMBOL_MAP.get(contractAddress);
                    if(symbol != null) {
                        map.put(symbol, NumberUtils.toDouble(priceUsd));
                    }
                }
            }

        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public String getBlockChainSymbol() {
        return Constants.CFX_BLOCKCHAIN_SYMBAL;
    }
}
