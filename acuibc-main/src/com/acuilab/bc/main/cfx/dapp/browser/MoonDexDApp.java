package com.acuilab.bc.main.cfx.dapp.browser;

import com.acuilab.bc.main.util.Constants;
import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;

/**
 *
 * @author 78429
 */
public class MoonDexDApp extends CfxBrowserDApp {

    @Override
    public void init() {
    }
    
    @Override
    public String getName() {
        return "MoonDEX";
    }
    
    @Override
    public String getType() {
	return "交易所";
    }

    @Override
    public String getBlockChainSymbol() {
	return Constants.CFX_BLOCKCHAIN_SYMBAL;
    }

    @Override
    public String getDesc() {
        return "MoonDEX是Conflux生态落地的首个去中心化交易平台";
    }

    @Override
    public ImageIcon getImageIcon() {
	return ImageUtilities.loadImageIcon("/resource/dapp/moon16.png", false);
    }

    @Override
    public String getUrl() {
        return "https://moondex.io/";
    }
}
