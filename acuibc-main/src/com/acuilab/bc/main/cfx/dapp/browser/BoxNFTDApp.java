package com.acuilab.bc.main.cfx.dapp.browser;

import com.acuilab.bc.main.util.Constants;
import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;

/**
 *
 * @author 78429
 */
public class BoxNFTDApp extends CfxBrowserDApp {

    @Override
    public void init() {
    }
    
    @Override
    public String getName() {
        return "NFT Box";
    }
    
    @Override
    public String getType() {
	return "NFT";
    }

    @Override
    public String getBlockChainSymbol() {
	return Constants.CFX_BLOCKCHAIN_SYMBAL;
    }

    @Override
    public String getDesc() {
        return "我们是市场平台，艺术家、藏家、可以去设置合理的价格来交易他们的产品";
    }

    @Override
    public ImageIcon getImageIcon() {
	return ImageUtilities.loadImageIcon("/resource/dapp/boxnft16.png", false);
    }

    @Override
    public String getUrl() {
        return "https://www.boxnft.io/";
    }
}
