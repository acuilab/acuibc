package com.acuilab.bc.main.cfx.dapp.tinyversus;

import com.acuilab.bc.main.cfx.dapp.guguo.*;
import com.acuilab.bc.main.dapp.IDApp;
import com.acuilab.bc.main.util.Constants;
import javax.swing.ImageIcon;
import org.openide.util.ImageUtilities;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author 78429
 */
public class TinyVersusDApp implements IDApp {

    @Override
    public void init() {
    }
    
    @Override
    public String getName() {
        return "小小乱斗";
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
        return "小小乱斗。";
    }

    @Override
    public ImageIcon getImageIcon() {
	return ImageUtilities.loadImageIcon("tinyversus16.png", false);
    }

    @Override
    public void launch(String address, String privateKey) throws Exception {
        TopComponent tc = WindowManager.getDefault().findTopComponent("TinyVersusTopComponent");
        if(tc != null) {
	    ImageIcon imageIcon = getImageIcon();
	    if(imageIcon != null) {
		tc.setIcon(getImageIcon().getImage());
	    }
            tc.open();
            tc.requestActive();
        }
    }

    @Override
    public boolean needWalletSpecified() {
        return false;
    }
    
    
}
