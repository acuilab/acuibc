package com.acuilab.bc.main.action;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.awt.CheckForUpdatesProvider;
import org.openide.util.Lookup;

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
        final CheckForUpdatesProvider checkForUpdatesProvider = Lookup.getDefault().lookup(CheckForUpdatesProvider.class);
        assert checkForUpdatesProvider != null : "An instance of CheckForUpdatesProvider found in Lookup: " + Lookup.getDefault();
        if (checkForUpdatesProvider != null) {
            checkForUpdatesProvider.openCheckForUpdatesWizard(true);
        }
    }
}