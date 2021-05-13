package com.acuilab.bc.main.cfx.dapp.browser;

import com.acuilab.bc.main.util.Constants;
import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;

/**
 *
 * @author 78429
 */
public class GovernanceDApp extends CfxBrowserDApp {

    @Override
    public void init() {
    }
    
    @Override
    public String getName() {
        return "CFX质押和投票";
    }
    
    @Override
    public String getType() {
	return "质押服务";
    }

    @Override
    public String getBlockChainSymbol() {
	return Constants.CFX_BLOCKCHAIN_SYMBAL;
    }

    @Override
    public String getDesc() {
        return "CFX的质押和投票(官方)";
    }

    @Override
    public ImageIcon getImageIcon() {
	return ImageUtilities.loadImageIcon("/resource/dapp/cfx16.png", false);
    }

    @Override
    public String getUrl() {
        return "https://governance.confluxnetwork.org/zh/";
    }
}
