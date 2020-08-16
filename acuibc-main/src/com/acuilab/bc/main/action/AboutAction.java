package com.acuilab.bc.main.action;

import com.acuilab.bc.main.ui.AboutDialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JDialog;

/**
 *
 * @author admin
 */
public class AboutAction extends AbstractAction {
    
    public AboutAction() {
        putValue(NAME, "关于");
//        putValue(SMALL_ICON, new javax.swing.ImageIcon(getClass().getResource("/cast/c514/padda/system/action/about.png")));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        JDialog aboutDlg = new AboutDialog(null, true);
        aboutDlg.setVisible(true);
    }
}