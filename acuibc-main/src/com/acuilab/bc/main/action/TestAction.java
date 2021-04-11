package com.acuilab.bc.main.action;

import com.acuilab.bc.main.cfx.dapp.browser.CfxBrowserDAppTopComponent;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;

/**
 *
 * @author admin
 */
public class TestAction extends AbstractAction {
    
    public TestAction() {
        putValue(NAME, "测试专用");
//        putValue(SMALL_ICON, new javax.swing.ImageIcon(getClass().getResource("/resource/wallet16.png")));
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        CfxBrowserDAppTopComponent tc = new CfxBrowserDAppTopComponent();
        tc.open();
        tc.requestActive();
    }
}