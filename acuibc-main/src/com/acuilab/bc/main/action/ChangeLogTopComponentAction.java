package com.acuilab.bc.main.action;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author admin
 */
public class ChangeLogTopComponentAction extends AbstractAction {
    
    public ChangeLogTopComponentAction() {
        putValue(NAME, "更新记录");
//        putValue(SMALL_ICON, new javax.swing.ImageIcon(getClass().getResource("/resource/wallet16.png")));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
	TopComponent tc = WindowManager.getDefault().findTopComponent("ChangeLogTopComponent");
	if(tc != null) {
	    tc.open();
	    tc.requestActive();
	}
    }
}