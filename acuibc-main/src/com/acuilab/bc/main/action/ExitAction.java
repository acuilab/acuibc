package com.acuilab.bc.main.action;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.LifecycleManager;

/**
 *
 * @author admin
 */
public class ExitAction extends AbstractAction {
    
    public ExitAction() {
        putValue(NAME, "退出");
//        putValue(SMALL_ICON, new javax.swing.ImageIcon(getClass().getResource("/cast/c514/padda/system/action/about.png")));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        LifecycleManager.getDefault().exit();
    }
}