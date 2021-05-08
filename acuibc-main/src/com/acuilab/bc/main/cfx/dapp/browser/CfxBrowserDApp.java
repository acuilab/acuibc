package com.acuilab.bc.main.cfx.dapp.browser;

import com.acuilab.bc.main.dapp.IDApp;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;
import org.apache.commons.lang3.StringUtils;

/**
 * 
 * @author admin
 */
public abstract class CfxBrowserDApp implements IDApp {

    @Override
    public void launch(String address, String privateKey) throws Exception {
        // java.lang.IllegalStateException: Problem in some module which uses Window System: 
        // Window System API is required to be called from AWT thread only, see http://core.netbeans.org/proposals/threading/
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                String name = getName() + "(" + StringUtils.substring(address, StringUtils.length(address) - 4) + ")";
                CfxBrowserDAppTopComponent tc = new CfxBrowserDAppTopComponent();
                tc.setName(name);
                tc.setToolTipText(getDesc());
                ImageIcon imageIcon = getImageIcon();
                if(imageIcon != null) {
                    tc.setIcon(getImageIcon().getImage());
                }
                tc.init(address, privateKey, name, getUrl(), getCustomJsBeforeInjectWeb3());
                tc.open();
                tc.requestActive();
            }
        });
    }
    
    /**
     * 页面加载完成后，注入web3之前执行的自定义js
     * @return 
     */
    public String getCustomJsBeforeInjectWeb3() {
        return "";
    }
    /**
     * 获得浏览器加载的dapp的URL地址
     * @return 
     */
    public abstract String getUrl();
}
