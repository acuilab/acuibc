package com.acuilab.bc.main.action;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author admin
 */
public class DappStoreTopComponentAction4Menu extends AbstractAction {
    
    public DappStoreTopComponentAction4Menu() {
        putValue(NAME, "DappStore");
        putValue(SMALL_ICON, new javax.swing.ImageIcon(getClass().getResource("/resource/dappstore16.png")));
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