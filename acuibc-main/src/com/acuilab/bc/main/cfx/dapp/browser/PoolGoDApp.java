package com.acuilab.bc.main.cfx.dapp.browser;

import com.acuilab.bc.main.util.Constants;
import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;

/**
 *
 * @author 78429
 */
public class PoolGoDApp extends CfxBrowserDApp {

    @Override
    public void init() {
    }
    
    @Override
    public String getName() {
        return "PoolGo";
    }
    
    @Override
    public String getType() {
	return "无损彩票";
    }

    @Override
    public String getBlockChainSymbol() {
	return Constants.CFX_BLOCKCHAIN_SYMBAL;
    }

    @Override
    public String getDesc() {
        return "PoolGo是一款开源、去中心化的无损幸运挖矿协议。";
    }

    @Override
    public ImageIcon getImageIcon() {
	return ImageUtilities.loadImageIcon("/resource/dapp/poolgo16.png", false);
    }

    @Override
    public String getUrl() {
        return "https://poolgo.finance/";
    }
}
