package com.acuilab.bc.main.action;

import com.acuilab.bc.main.dapp.DebugBrowserDAppTopComponent;
import com.acuilab.bc.main.dapp.BrowserInfo;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;

/**
 *
 * @author admin
 */
public class RefreshBrowserDAppAction extends AbstractAction implements LookupListener {
    
    private final Lookup.Result<BrowserInfo> result;
    private BrowserInfo browserInfo;
    
    public RefreshBrowserDAppAction() {
        putValue(NAME, "刷新");
        putValue(SMALL_ICON, new javax.swing.ImageIcon(getClass().getResource("/resource/refresh32.png")));

        result = Utilities.actionsGlobalContext().lookupResult(BrowserInfo.class);
        result.addLookupListener(this);
        
        setEnabled(false);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        browserInfo.getBrowser().reload();
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                 setEnabled(!result.allInstances().isEmpty());
            }
            
        });
       
        if(result.allInstances().isEmpty()) {
            return; 
        }
        
        browserInfo = result.allInstances().iterator().next();
    }
}