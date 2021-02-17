package com.acuilab.bc.main.action;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author admin
 */
public class WalletListTopComponentAction extends AbstractAction {
    
    public WalletListTopComponentAction() {
        putValue(NAME, "钱包列表");
        putValue(SMALL_ICON, new javax.swing.ImageIcon(getClass().getResource("/resource/wallet16.png")));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
	TopComponent tc = WindowManager.getDefault().findTopComponent("WalletListTopComponent");
	if(tc != null) {
	    tc.open();
	    tc.requestActive();
	}
    }
}