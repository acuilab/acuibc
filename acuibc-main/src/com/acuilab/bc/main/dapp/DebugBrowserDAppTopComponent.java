package com.acuilab.bc.main.dapp;

import com.acuilab.bc.main.JxBrowserDisposer;
import com.google.common.io.Files;
import java.util.concurrent.atomic.AtomicInteger;
import org.openide.windows.TopComponent;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.BrowserContextParams;
import com.teamdev.jxbrowser.chromium.BrowserType;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import java.awt.BorderLayout;
import java.io.File;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Top component which displays something.
 */
@TopComponent.Description(
        preferredID = "DebugBrowserDAppTopComponent",
        iconBase="resource/debug16.png",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ServiceProvider(service=JxBrowserDisposer.class)
public final class DebugBrowserDAppTopComponent extends TopComponent implements JxBrowserDisposer {
    private static final Logger LOG = Logger.getLogger(DebugBrowserDAppTopComponent.class.getName());
    
    private final Browser browser;
    private final BrowserView view;
    
    private static final AtomicInteger ID = new AtomicInteger();
    private final String PREFERRED_ID;  // 20200802
    
    private final File dataDir = Files.createTempDir();
    
    public DebugBrowserDAppTopComponent() {
        initComponents();
//        BlockChain bc = BlockChainManager.getDefault().getBlockChain(wallet.getBlockChainSymbol());
//        this.setIcon(bc.getIconImage(16));
        putClientProperty(TopComponent.PROP_CLOSING_DISABLED, Boolean.FALSE);
        putClientProperty(TopComponent.PROP_DRAGGING_DISABLED, Boolean.FALSE);
        putClientProperty(TopComponent.PROP_MAXIMIZATION_DISABLED, Boolean.FALSE);
        putClientProperty(TopComponent.PROP_UNDOCKING_DISABLED, Boolean.FALSE);

	// http://acuilab.com:8080/articles/2021/02/09/1612840191556.html
	// 解决JxBrowser中BrowserView控件覆盖其他控件的办法
	browser = new Browser(BrowserType.LIGHTWEIGHT, new BrowserContext(new BrowserContextParams(dataDir.getAbsolutePath())));
	view = new BrowserView(browser);
	this.add(view, BorderLayout.CENTER);
        
        // 这是一个新打开的窗口，生成新的窗口id并保存
        int id = ID.incrementAndGet();
        PREFERRED_ID = NbBundle.getMessage(DebugBrowserDAppTopComponent.class, "ID_DebugBrowserDAppTopComponent", Integer.toString(id));
    }
    
    public void init(DebugInfo debugInfo) {
        browser.loadURL(debugInfo.getRemoteDebuggingUrl());
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
    }

    @Override
    public void componentClosed() {
    }
    
    // 覆盖此方法，为TopComponent设置preferredID
    // Subclasses are encouraged to override this method to provide preferred value for unique TopComponent ID returned 
    // by WindowManager.findTopComponentID(org.openide.windows.TopComponent). Returned value should be a String, preferably 
    // describing semantics of TopComponent subclass, such as "PieChartViewer" or "HtmlEditor" etc. Value is then used by window 
    // system as prefix value for creating unique TopComponent ID. Returned String value should be preferably unique, but need not be. 
    @Override
    protected String preferredID() {
        return PREFERRED_ID;
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }
    
    public void executeJavaScript(String js) {
        browser.executeJavaScript(js);
    }
    
    @Override
    public void disposeBrowser() {
        
        // 清空临时文件夹
        FileUtils.deleteQuietly(dataDir);
        
        if(browser != null) {
            browser.dispose();
        }
    }
}