package com.acuilab.bc.main.cfx.dapp.browser;

import com.acuilab.bc.main.JxBrowserDisposer;
import com.acuilab.bc.main.wallet.WalletTopComponent;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jdesktop.swingx.JXButton;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
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
    private final Browser browser;
    private final BrowserView view;
    
    private static final AtomicInteger ID = new AtomicInteger();
    private final String PREFERRED_ID;  // 20200802
    
    private final File dataDir = Files.createTempDir();
    
    // 从lasspath加载conflux.js
    private static String confluxJs;
    static {
	try {
	    confluxJs = IOUtils.toString(CfxBrowserDAppTopComponent.class.getResourceAsStream("/resource/dapp/conflux.js"), Charsets.UTF_8);
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
        System.out.println("dataDir ========================== " + dataDir.getAbsolutePath());
	browser = new Browser(BrowserType.LIGHTWEIGHT, new BrowserContext(new BrowserContextParams(dataDir.getAbsolutePath())));
	view = new BrowserView(browser);
	this.add(view, BorderLayout.CENTER);
        
        // 这是一个新打开的窗口，生成新的窗口id并保存
        int id = ID.incrementAndGet();
        PREFERRED_ID = NbBundle.getMessage(WalletTopComponent.class, "ID_WalletTopComponent", Integer.toString(id));
        
//        // 工具栏
//        JXButton debugBtn = new JXButton("调试");
//        debugBtn.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                DebugDialog dlg = new DebugDialog(null, browser.getRemoteDebuggingURL());
//                dlg.setVisible(true);
//            }
//            
//        });
//        toolbar.add(debugBtn);
    }
    
    public void init(String url, String customJs) {
        browser.addLoadListener(new LoadAdapter() {
            @Override
            public void onFinishLoadingFrame(FinishLoadingEvent event) {
                if (event.isMainFrame()) {
		    System.out.println("Main frame has finished loading");

                    // custom js
                    browser.executeJavaScript(customJs);
                    
                    // inject web3
                    JSValue window = browser.executeJavaScriptAndReturnValue("window");
                    OpenJoyBridge openJoyBridge = new OpenJoyBridge(CfxBrowserDAppTopComponent.this);
                    window.asObject().setProperty("openJoyBridge", openJoyBridge);
		    
		    // 执行conflux.js
		    browser.executeJavaScript(confluxJs);
                    
                    System.out.println("remoteDebuggingUrl========================" + browser.getRemoteDebuggingURL());
                    
//            // Creates another Browser instance and loads the remote Developer
//            // Tools URL to access HTML inspector.
//            Browser browser2 = new Browser();
//            BrowserView view2 = new BrowserView(browser2);
//
//            JFrame frame2 = new JFrame();
//            frame2.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
//            frame2.add(view2, BorderLayout.CENTER);
//            frame2.setSize(700, 500);
//            frame2.setLocationRelativeTo(null);
//            frame2.setVisible(true);
//            browser2.loadURL(browser.getRemoteDebuggingURL());
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
