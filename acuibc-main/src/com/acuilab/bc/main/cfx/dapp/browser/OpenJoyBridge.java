package com.acuilab.bc.main.cfx.dapp.browser;

import com.acuilab.bc.main.cfx.CFXExtend;
import com.acuilab.bc.main.dapp.WalletSelectorDialog;
import com.acuilab.bc.main.ui.ConfirmDialog;
import com.acuilab.bc.main.wallet.Wallet;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigInteger;
import javax.swing.SwingUtilities;
import org.apache.commons.lang3.StringUtils;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author admin
 */
public class OpenJoyBridge {
    
    private final CfxBrowserDAppTopComponent tc;
    private final String address;
    private final String privateKey;

    public OpenJoyBridge(CfxBrowserDAppTopComponent tc, String address, String privateKey) {
        this.tc = tc;
        this.address = address;
        this.privateKey = privateKey;
    }
    
    /**
     * 返回默认账户地址
     * FC质押会自动使用这个地址设置，导致无法切换到用户选中的地址，这里返回一个空值
     * @param type
     * @return 
     */
    @com.teamdev.jxbrowser.chromium.JSAccessible
    public String getChainAddress(String type) {
	return address;
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
                System.out.println("jsonStr================================================" + jsonStr);
                
                // 解析json
                ObjectMapper mapper = new ObjectMapper();
                try {
                    JsonNode rootNode = mapper.readTree(jsonStr);
                    
                    JsonNode typeNode = rootNode.get("type");
                    String type = typeNode.asText();
                    JsonNode resolverNode = rootNode.get("resolver");
                    long resolver = resolverNode.asLong();
                    JsonNode payloadNode = rootNode.get("payload");
                    
                    if(StringUtils.equals("requestAccounts", type)) {
                        
//                        // UI线程获得钱包
//                        SwingUtilities.invokeAndWait(new Runnable() {
//                            @Override
//                            public void run() {
//                                WalletSelectorDialog dlg = new WalletSelectorDialog(null);
//                                dlg.setVisible(true);
//                                if(dlg.getReturnStatus() == ConfirmDialog.RET_OK) {
//                                    Wallet wallet = dlg.getSelectedWallet();
//                                    address[0] = wallet.getAddress();
//                                }
//                            }
//                            
//                        });
                        
                        ObjectMapper om = new ObjectMapper();
                        ObjectNode on = om.createObjectNode();
                        on.put("jsonrpc", "2.0");
                        on.put("id", resolver);
                        on.put("result", address);
                        
                        System.out.println("requestAccounts on==========================================" + on);
//String jsStr = "(function() {var event; var data = {'data': '"+ on +"'};  try { event = new MessageEvent('message', data); } catch(e){ event = document.createEvent('MessageEvent'); event.initMessageEvent('message', true, true, data.data, data.orgin, data.lastEventId, data.source);} document.dispatchEvent(event); })();";
//                        tc.executeJavaScript(jsStr);

                        tc.executeJavaScript("conflux.callbacks.get("+ resolver +")(null, "+ on +");");
                    } else if(StringUtils.equals("signTransaction", type)) {
                        
                        JsonNode fromNode = payloadNode.get("from");
                        String from = fromNode.asText();
                        JsonNode gasNode = payloadNode.get("gas");
                        BigInteger gas = gasNode==null ? null : new BigInteger(StringUtils.substringAfter(gasNode.asText(), "0x"), 16);
                        String to = payloadNode.get("to").asText();
                        JsonNode valueNode = payloadNode.get("value");
                        BigInteger value = valueNode == null ? null : new BigInteger(StringUtils.substringAfter(valueNode.asText(), "0x"), 16);
                        JsonNode storageLimitNode = payloadNode.get("storageLimit");
                        BigInteger storageLimit = storageLimitNode == null ? null : new BigInteger(StringUtils.substringAfter(storageLimitNode.asText(), "0x"), 16);
                        String data = payloadNode.get("data").asText();
                        
                        // TODO: 显示确认对话框
                        
                        CFXExtend cfxExtend = Lookup.getDefault().lookup(CFXExtend.class);
                        String hash = cfxExtend.send(privateKey, 
                                from, 
                                gas, 
                                to, 
                                value, 
                                storageLimit, 
                                data);
                        
                        ObjectMapper om = new ObjectMapper();
                        ObjectNode on = om.createObjectNode();
                        on.put("jsonrpc", "2.0");
                        on.put("id", resolver);
                        on.put("result", hash);
                        
                        System.out.println("signTransaction on==========================================" + on);
                        
                        tc.executeJavaScript("conflux.callbacks.get("+ resolver +")(null, "+ on +");");
                    } else if(StringUtils.equals("signTypedMessage", type)) {
			
			String data = payloadNode.get("data").asText();
			
			CFXExtend cfxExtend = Lookup.getDefault().lookup(CFXExtend.class);
			String signData = cfxExtend.sign(privateKey, data);
			
                        ObjectMapper om = new ObjectMapper();
                        ObjectNode on = om.createObjectNode();
                        on.put("jsonrpc", "2.0");
                        on.put("id", resolver);
                        on.put("result", signData);
                        
                        System.out.println("signTypedMessage on==========================================" + on);
                        
                        tc.executeJavaScript("conflux.callbacks.get("+ resolver +")(null, "+ on +");");
		    }
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
	return result;
    }
}
