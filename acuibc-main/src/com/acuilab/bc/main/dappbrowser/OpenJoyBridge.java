package com.acuilab.bc.main.dappbrowser;

import java.math.BigInteger;

/**
 *
 * @author admin
 */
public class OpenJoyBridge {
    @com.teamdev.jxbrowser.chromium.JSAccessible
    @JSAccessible
    public String getChainAddress(String type) {
	System.out.println("getChainAddress =========================== " + str);
	return type;
    }
    
    @com.teamdev.jxbrowser.chromium.JSAccessible
    public String getChainId(String type) {
	System.out.println("getChainId =========================== " + str);
	return type;
    }
    
    @com.teamdev.jxbrowser.chromium.JSAccessible
    public String getChainRpcUrl(String type) {
	System.out.println("getChainRpcUrl =========================== " + str);
	return type;
    }
    
    @com.teamdev.jxbrowser.chromium.JSAccessible
    public String postMessage(String paramStr) {
	System.out.println("paramStr =========================== " + paramStr);
	String result = "";
	
	
	return result;
    }
    
    @com.teamdev.jxbrowser.chromium.JSAccessible
    public String alert(String firstName) {
	System.out.println("OpenJoyBridge.alert() ========================== " + firstName);
	return "Hello " + firstName + "!";
    }
}
