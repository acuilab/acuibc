package com.acuilab.bc.cfx.pricegrabber;

import com.acuilab.bc.main.coin.IPriceGrabber;
import com.acuilab.bc.main.util.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

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
            // 解析json
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(body.string());
            
            for (final JsonNode objNode : root) {
                System.out.println("base_currency=" + objNode.get("base_currency").asText());
            }
            
            
        }
    }

    @Override
    public String getBlockChainSymbol() {
        return Constants.CFX_BLOCKCHAIN_SYMBAL;
    }

}
