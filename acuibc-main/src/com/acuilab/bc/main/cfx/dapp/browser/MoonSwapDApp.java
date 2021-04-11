package com.acuilab.bc.main.cfx.dapp.browser;

import com.acuilab.bc.main.util.Constants;
import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;

/**
 *
 * @author 78429
 */
public class MoonSwapDApp extends CfxBrowserDApp {

    @Override
    public void init() {
    }
    
    @Override
    public String getName() {
        return "MoonSwap";
    }
    
    @Override
    public String getType() {
	return "综合";
    }

    @Override
    public String getBlockChainSymbol() {
	return Constants.CFX_BLOCKCHAIN_SYMBAL;
    }

    @Override
    public String getDesc() {
        return "下一代去中心化交易协议！高速0Gas！基于Conflux底层网络，以太坊L2解决方案的自动做市去中心化交易平台！";
    }

    @Override
    public ImageIcon getImageIcon() {
	return ImageUtilities.loadImageIcon("/resource/dapp/moon16.png", false);
    }

    @Override
    public String getUrl() {
        return "https://moonswap.fi/";
    }
}
