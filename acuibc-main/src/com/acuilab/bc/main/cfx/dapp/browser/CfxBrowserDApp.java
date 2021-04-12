package com.acuilab.bc.main.cfx.dapp.browser;

import com.acuilab.bc.main.dapp.IDApp;
import javax.swing.ImageIcon;

/**
 * 
 * @author admin
 */
public abstract class CfxBrowserDApp implements IDApp {

    @Override
    public void launch(String param) throws Exception {
        CfxBrowserDAppTopComponent tc = new CfxBrowserDAppTopComponent();
        tc.setName(getName());
        tc.setToolTipText(getDesc());
        ImageIcon imageIcon = getImageIcon();
        if(imageIcon != null) {
            tc.setIcon(getImageIcon().getImage());
        }
        tc.init(getUrl(), getCustomJsBeforeInjectWeb3());
        tc.open();
        tc.requestActive();
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
    
    @Override
    public boolean isInternal() {
        return true;
    }
}
