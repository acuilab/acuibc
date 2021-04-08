package com.acuilab.bc.main.dappbrowser;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author admin
 */
public class OpenJoyBridge {
    
    private boolean isConnected = true;
    
    /**
     * 返回账户地址
     * @param type
     * @return 
     */
    @com.teamdev.jxbrowser.chromium.JSAccessible
    public String getChainAddress(String type) {
	return "cfx:aaptdrxyfay01takr315e43uws4cy3h4m63vawh5je";
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
                
                System.out.println("");
                
//                system.out.println(json.tojsonstring());
//                
//                result = new confluxsdk(context, laywebview).deal(json);
            }
        }
	return result;
    }
}
