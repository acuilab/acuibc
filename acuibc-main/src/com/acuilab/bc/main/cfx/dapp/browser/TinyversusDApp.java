package com.acuilab.bc.main.cfx.dapp.browser;

import com.acuilab.bc.main.util.Constants;
import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;

/**
 *
 * @author 78429
 */
public class TinyversusDApp extends CfxBrowserDApp {

    @Override
    public void init() {
    }
    
    @Override
    public String getName() {
        return "Tinyversus";
    }
    
    @Override
    public String getType() {
	return "游戏";
    }

    @Override
    public String getBlockChainSymbol() {
	return Constants.CFX_BLOCKCHAIN_SYMBAL;
    }

    @Override
    public String getDesc() {
        return "FLUX是去中心化、安全、非托管的数字加密资产抵押借贷协议。在FLUX的货币市场上，用户可以存入特定的加密资产赚取利息，也可以通过超额抵押资产并支付一定的利息借出某种加密资产。";
    }

    @Override
    public ImageIcon getImageIcon() {
	return ImageUtilities.loadImageIcon("/resource/dapp/flux16.png", false);
    }

    @Override
    public String getUrl() {
        return "https://test.tinyversus.com/store";
    }
}
