package com.acuilab.bc.main.cfx.dapp.solidity;

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
public class Solidity2JavaDApp implements IDApp {

    @Override
    public void init() {
    }
    
    @Override
    public String getName() {
        return "代码生成器";
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
        return "基于Solidity文件生成Java代码";
    }

    @Override
    public ImageIcon getImageIcon() {
	return ImageUtilities.loadImageIcon("/resource/dapp/code16.png", false);
    }

    @Override
    public void launch(String address, String privateKey) throws Exception {
        TopComponent tc = WindowManager.getDefault().findTopComponent("Solidity2JavaTopComponent");
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
