package com.acuilab.bc.main.dappbrowser;

import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author admin
 */
public class OpenJoyBridge {
    
    private DAppBrowserTopComponent tc;
    private boolean isConnected;

    public OpenJoyBridge(DAppBrowserTopComponent tc) {
        this.tc = tc;
        this.isConnected = false;
    }
    
    /**
     * 返回账户地址
     * @param type
     * @return 
     */
    @com.teamdev.jxbrowser.chromium.JSAccessible
    public String getChainAddress(String type) {
	return "cfx:aapvvj1gt07k5d8vs18w2z1ymhkenfw2k2smvbz674";
    }
    
    @com.teamdev.jxbrowser.chromium.JSAccessible
    public String getChainId(String type) {
	return "1029";
    }
    
    @com.teamdev.jxbrowser.chromium.JSAccessible
    public String getChainRpcUrl(String type) {
	return "https://mainnet-rpc.conflux-chain.org.cn/v2";
    }
    
    @com.teamdev.jxbrowser.chromium.JSAccessible
    public String postMessage(String paramStr) {
        
	String result = "";
	
	if(StringUtils.isNotBlank(paramStr)) {
            if(StringUtils.startsWith(paramStr, "conflux")) {
                // 调用Conflux接口处理类处理
                String jsonStr = StringUtils.substring(paramStr, 8);
                
                System.out.println("jsonStr=================" + jsonStr);
                
//                system.out.println(json.tojsonstring());
//                
//                result = new confluxsdk(context, laywebview).deal(json);

                String resolver = StringUtils.substringBetween(jsonStr, "\"resolver\":", "}");
                System.out.println("resolver=" + resolver);
                
                String res = "{\"jsonrpc\": \"2.0\",\"id\": " + resolver + ", \"result\":\"cfx:aapvvj1gt07k5d8vs18w2z1ymhkenfw2k2smvbz674\"}";
                

                String jsStr = "conflux.callbacks.get("+ resolver +")(null, "+ res +");";

                tc.executeJavaScript(jsStr);
            }
        }
	return result;
    }
}
