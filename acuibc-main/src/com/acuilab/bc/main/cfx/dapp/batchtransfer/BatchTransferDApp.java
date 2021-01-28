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
    public String getBlockChainSymbol() {
	return Constants.CFX_BLOCKCHAIN_SYMBAL;
    }

    @Override
    public String getDesc() {
        return "网上银行批量转账方法,批量转账多用于一次给同一银行账号的多人转账";
    }

    @Override
    public ImageIcon getImageIcon() {
	return ImageUtilities.loadImageIcon("/resource/dapp/bee16.png", false);
    }

    @Override
    public void launch(String param) throws Exception {
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

    @Override
    public boolean isInternal() {
        return true;
    }
}
