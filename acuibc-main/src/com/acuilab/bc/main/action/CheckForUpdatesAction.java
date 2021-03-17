package com.acuilab.bc.main.action;

import com.acuilab.bc.main.ui.CheckForUpdatesDialog;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.util.RequestProcessor;

/**
 *
 * @author admin
 */
public class CheckForUpdatesAction extends AbstractAction {
    
    public CheckForUpdatesAction() {
        putValue(NAME, "检查更新");
//        putValue(SMALL_ICON, new javax.swing.ImageIcon(getClass().getResource("/cast/c514/padda/system/action/about.png")));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
//        final CheckForUpdatesProvider checkForUpdatesProvider = Lookup.getDefault().lookup(CheckForUpdatesProvider.class);
//        assert checkForUpdatesProvider != null : "An instance of CheckForUpdatesProvider found in Lookup: " + Lookup.getDefault();
//        checkForUpdatesProvider.openCheckForUpdatesWizard(true);
        CheckForUpdatesDialog dlg = new CheckForUpdatesDialog(null);
        dlg.setVisible(true);
        if(dlg.getReturnStatus() == CheckForUpdatesDialog.RET_OK) {
            // 执行更新
            RequestProcessor.getDefault().post(new Runnable() {
                @Override
                public void run() {
                    dlg.installModules();
                }
                
            }, 1000);
        }
    }
}