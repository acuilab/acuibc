package com.acuilab.bc.main.cfx.dapp.browser;

import com.acuilab.bc.main.JxBrowserDisposer;
import com.acuilab.bc.main.dapp.DebugInfo;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.util.concurrent.atomic.AtomicInteger;
import org.openide.windows.TopComponent;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.BrowserContextParams;
import com.teamdev.jxbrowser.chromium.BrowserType;
import com.teamdev.jxbrowser.chromium.JSValue;
import com.teamdev.jxbrowser.chromium.PopupContainer;
import com.teamdev.jxbrowser.chromium.PopupHandler;
import com.teamdev.jxbrowser.chromium.PopupParams;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;

/**
 * Top component which displays something.
 */
@TopComponent.Description(
        preferredID = "CfxBrowserDAppTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE",
        persistenceType = TopComponent.PERSISTENCE_NEVER
)
@TopComponent.Registration(mode = "editor", openAtStartup = false)
@ServiceProvider(service=JxBrowserDisposer.class)
public final class CfxBrowserDAppTopComponent extends TopComponent implements JxBrowserDisposer {
    private static final Logger LOG = Logger.getLogger(CfxBrowserDAppTopComponent.class.getName());
    
    private String address;
    private String privateKey;
    
    private final Browser browser;
    private final BrowserView view;
    
    private static final AtomicInteger ID = new AtomicInteger();
    private final String PREFERRED_ID;  // 20200802
    
    private final File dataDir = Files.createTempDir();
    
    private final InstanceContent content = new InstanceContent();
    
    // 从lasspath加载conflux.js
    private static String confluxJs;
    private static String loadingHtml;
    static {
	try {
	    confluxJs = IOUtils.toString(CfxBrowserDAppTopComponent.class.getResourceAsStream("/resource/dapp/conflux.js"), Charsets.UTF_8);
            loadingHtml = IOUtils.toString(CfxBrowserDAppTopComponent.class.getResourceAsStream("/resource/dapp/loading.html"), Charsets.UTF_8);
	} catch (IOException ex) {
	    Exceptions.printStackTrace(ex);
	}
    }

    public CfxBrowserDAppTopComponent() {
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
        PREFERRED_ID = NbBundle.getMessage(CfxBrowserDAppTopComponent.class, "ID_CfxBrowserDAppTopComponent", Integer.toString(id));
        
        browser.loadHTML(loadingHtml);
        
        associateLookup(new AbstractLookup(content)); 
    }
    
    public void init(String address, String privateKey, String name, String url, String customJs) {
        this.address = address;
        this.privateKey = privateKey;
        browser.addLoadListener(new LoadAdapter() {
            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent event) {
                if (event.isMainFrame()) {
		    System.out.println("Main frame has finished loading");

                    // custom js
                    browser.executeJavaScript(customJs);
                    
                    // inject web3
                    JSValue window = browser.executeJavaScriptAndReturnValue("window");
                    OpenJoyBridge openJoyBridge = new OpenJoyBridge(CfxBrowserDAppTopComponent.this, address, privateKey);
                    window.asObject().setProperty("openJoyBridge", openJoyBridge);
		    
		    // 执行conflux.js
		    browser.executeJavaScript(confluxJs);
                    
                    content.set(Collections.singleton(new DebugInfo(name + "(调试)", browser.getRemoteDebuggingURL())), null);
                }
            }
        });
        // 强制不弹出窗口
        browser.setPopupHandler(new PopupHandler() {
            @Override
            public PopupContainer handlePopup(PopupParams pp) {
                browser.loadURL(pp.getURL());
                return null;
            }
            
        });
        
        browser.loadURL(url);
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
