package com.acuilab.bc.main.cfx.dapp.batchtransfer;

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
public class BatchTransferDApp implements IDApp {

    @Override
    public void init() {
    }
    
    @Override
    public String getName() {
        return "批量转账";
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
        return "支持Conflux链上批量发送CFX及其他erc20代币";
    }

    @Override
    public ImageIcon getImageIcon() {
	return ImageUtilities.loadImageIcon("/resource/dapp/batchtransfer16.png", false);
    }

    @Override
    public void launch(String address, String privateKey) throws Exception {
        TopComponent tc = WindowManager.getDefault().findTopComponent("BatchTransferTopComponent");
        if(tc != null) {
	    ImageIcon imageIcon = getImageIcon();
	    if(imageIcon != null) {
		tc.setIcon(getImageIcon().getImage());
	    }
            tc.open();
            tc.requestActive();
        }
    }
}
