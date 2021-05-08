package com.acuilab.bc.main.action;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author admin
 */
public class DappStoreTopComponentAction4Toolbar extends AbstractAction {
    
    public DappStoreTopComponentAction4Toolbar() {
        putValue(NAME, "DappStore");
        putValue(SMALL_ICON, new javax.swing.ImageIcon(getClass().getResource("/resource/dappstore32.png")));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
	TopComponent tc = WindowManager.getDefault().findTopComponent("InternalDAppListTopComponent");
	if(tc != null) {
	    tc.open();
	    tc.requestActive();
	}
    }
}