package com.acuilab.bc.main.action;

import com.acuilab.bc.main.ui.OptionDialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author admin
 */
public class OptionAction extends AbstractAction {
    
    public OptionAction() {
        putValue(NAME, "选项");
//        putValue(SMALL_ICON, new javax.swing.ImageIcon(getClass().getResource("/cast/c514/padda/system/action/about.png")));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        OptionDialog optionDlg = new OptionDialog(null, true);
        optionDlg.setVisible(true);
        if(optionDlg.getReturnStatus() == OptionDialog.RET_OK) {
            // 保存所有设置
            
        }
    }
}