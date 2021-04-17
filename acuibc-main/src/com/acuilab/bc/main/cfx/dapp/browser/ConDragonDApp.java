package com.acuilab.bc.main.cfx.dapp.browser;

import com.acuilab.bc.main.util.Constants;
import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;

/**
 *
 * @author 78429
 */
public class ConDragonDApp extends CfxBrowserDApp {

    @Override
    public void init() {
    }
    
    @Override
    public String getName() {
        return "ConDragon";
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
        return "ConDragon是Conflux生态首款DeFi+NFT+RPG游戏，一站式体验NFT挖矿、拍卖、交易等功能。";
    }

    @Override
    public ImageIcon getImageIcon() {
	return ImageUtilities.loadImageIcon("/resource/dapp/condragon16.png", false);
    }

    @Override
    public String getUrl() {
        return "https://condragon.com/";
    }
}
