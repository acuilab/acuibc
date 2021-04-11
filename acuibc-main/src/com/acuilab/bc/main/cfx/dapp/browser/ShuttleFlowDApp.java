package com.acuilab.bc.main.cfx.dapp.browser;

import com.acuilab.bc.main.util.Constants;
import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;

/**
 *
 * @author 78429
 */
public class ShuttleFlowDApp extends CfxBrowserDApp {

    @Override
    public void init() {
    }
    
    @Override
    public String getName() {
        return "ShuttleFlow";
    }
    
    @Override
    public String getType() {
	return "工具";
    }

    @Override
    public String getBlockChainSymbol() {
	return Constants.CFX_BLOCKCHAIN_SYMBAL;
    }

    @Override
    public String getDesc() {
        return "ShuttleFlow是一个无需许可的资产跨链协议。";
    }

    @Override
    public ImageIcon getImageIcon() {
	return ImageUtilities.loadImageIcon("/resource/dapp/shuttleflow16.png", false);
    }

    @Override
    public String getUrl() {
        return "https://webview.shuttleflow.io/eth/shuttle/in";
    }
}
