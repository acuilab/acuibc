package com.acuilab.bc.main.action;

import com.acuilab.bc.main.dapp.DebugBrowserDAppTopComponent;
import com.acuilab.bc.main.dapp.DebugInfo;
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
public class DebugBrowserDAppAction extends AbstractAction implements LookupListener {
    
    private final Lookup.Result<DebugInfo> result;
    private DebugInfo debugInfo;
    
    public DebugBrowserDAppAction() {
        putValue(NAME, "调试");
        putValue(SMALL_ICON, new javax.swing.ImageIcon(getClass().getResource("/resource/debug32.png")));

        result = Utilities.actionsGlobalContext().lookupResult(DebugInfo.class);
        result.addLookupListener(this);
        
        setEnabled(false);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        DebugBrowserDAppTopComponent tc = new DebugBrowserDAppTopComponent();
        tc.setName(debugInfo.getTitle());
        tc.setToolTipText(debugInfo.getTitle());
        tc.init(debugInfo);
        tc.open();
        tc.requestActive();
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
        
        debugInfo = result.allInstances().iterator().next();
    }
}