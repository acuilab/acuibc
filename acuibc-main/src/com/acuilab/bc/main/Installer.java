package com.acuilab.bc.main;

import com.acuilab.bc.main.cfx.CFXExtend;
import com.acuilab.bc.main.manager.BlockChainManager;
import com.acuilab.bc.main.manager.CoinManager;
import com.acuilab.bc.main.manager.DAppManager;
import com.acuilab.bc.main.manager.NFTManager;
import static com.acuilab.bc.main.ui.CheckForUpdatesDialog.UC_NAME;
import com.acuilab.bc.main.ui.ConfirmDialog;
import com.acuilab.bc.main.util.Constants;
import com.acuilab.bc.main.util.Utils;
import com.google.common.io.Files;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.BrowserContext;
import com.teamdev.jxbrowser.chromium.BrowserContextParams;
import com.teamdev.jxbrowser.chromium.BrowserCore;
import com.teamdev.jxbrowser.chromium.BrowserPreferences;
import com.teamdev.jxbrowser.chromium.BrowserType;
import com.teamdev.jxbrowser.chromium.be;
import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;
import com.teamdev.jxbrowser.chromium.events.LoadAdapter;
import com.teamdev.jxbrowser.chromium.internal.Environment;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigInteger;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.commons.lang3.StringUtils;
import org.netbeans.api.autoupdate.UpdateUnit;
import org.netbeans.api.autoupdate.UpdateUnitProvider;
import org.netbeans.api.autoupdate.UpdateUnitProviderFactory;
import org.openide.awt.NotificationDisplayer;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.RequestProcessor;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall {
    
    private static final Logger LOG = Logger.getLogger(Installer.class.getName());
    private static Connection conn = null;
    
    // crack jxbrowser
    static {
        LOG.log(Level.INFO, "static thread name: {0}", Thread.currentThread().getName());

        try {
            Field e = be.class.getDeclaredField("e");
            e.setAccessible(true);
            Field f = be.class.getDeclaredField("f");
            f.setAccessible(true);
            Field modifersField = Field.class.getDeclaredField("modifiers");
            modifersField.setAccessible(true);
            modifersField.setInt(e, e.getModifiers() & ~Modifier.FINAL);
            modifersField.setInt(f, f.getModifiers() & ~Modifier.FINAL);
            e.set(null, new BigInteger("1"));
            f.set(null, new BigInteger("1"));
            modifersField.setAccessible(false);
        } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException | SecurityException e1) {
            e1.printStackTrace();
        }

    }

    @Override
    public void restored() {
	// 在配置文件gourd.conf中指定
//        System.setProperty("sun.java2d.noddraw", "true");
	// java.lang.IllegalArgumentException: Renderer extends the SubstanceDefaultTableCellRenderer but does not return one in its getTableCellRendererComponent() method
//        System.setProperty("insubstantial.looseTableCellRenderers", "true");
//        System.setProperty("insubstantial.checkEDT", "false");
//        System.setProperty("insubstantial.logEDT", "false");

BrowserPreferences.setChromiumSwitches("--remote-debugging-port=9222");

        // @see https://jxbrowser.support.teamdev.com/support/solutions/articles/9000013071-using-jxbrowser-in-javafx
        new Thread(new Runnable() {
            @Override
            public void run() {
                
                // jxBrowser
                if(Environment.isMac()) {
                    LOG.log(Level.INFO, "BrowserCore.initialize thread name: {0}", Thread.currentThread().getName());
                    BrowserCore.initialize();
                    LOG.log(Level.INFO, "BrowserCore.initialize completed");
                }            
            }
        
        }).start();
        
	// derby
	System.setProperty("derby.system.home", System.getProperty("netbeans.user", System.getProperty("user.home")) + File.separator + "databases");
	LOG.log(Level.INFO, "derby.system.home={0}", System.getProperty("derby.system.home"));
	initTables();
        
        // init
        BlockChainManager.getDefault();
        CoinManager.getDefault();
	NFTManager.getDefault();
	DAppManager.getDefault();
        
        // 设置主窗口标题
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
		try {
		    // 设置皮肤
		    JFrame.setDefaultLookAndFeelDecorated(true);
		    JDialog.setDefaultLookAndFeelDecorated(true);
		    String skinClassName = Utils.getSkinClassName();
		    LOG.log(Level.INFO, "skinName={0}", skinClassName);
		    UIManager.setLookAndFeel(skinClassName);
		    Utils.applyUIChanges(null);
		    
		    // 设置标题
		    WindowManager.getDefault().getMainWindow().setTitle(Constants.TITLE);
		    // 最大化显示
		    WindowManager.getDefault().getMainWindow().setExtendedState(JFrame.MAXIMIZED_BOTH);
		    
		    //chy：广告位，有广告则用，无广告则隐藏
		    //new ADDialog(null, false).setVisible(true);
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
		    Exceptions.printStackTrace(ex);
		}
		
		// 启动检查更新
		RequestProcessor.getDefault().post(new Runnable(){
		    @Override
		    public void run() {
			int count = 0;
			for (UpdateUnitProvider provider : UpdateUnitProviderFactory.getDefault().getUpdateUnitProviders(false)) {
			    try {
				if (StringUtils.equals(UC_NAME, provider.getName())) {
				    provider.refresh(null, true);

				    for (UpdateUnit u : provider.getUpdateUnits()) {
					if(!u.getAvailableUpdates().isEmpty()) {
					    count++;
					}
				    }
				}
			    } catch (IOException ex) {
				LOG.severe(ex.getMessage());
			    }
			}

			if(count > 0) {
			    // 提示用户检查更新
			    NotificationDisplayer.getDefault().notify(
				    "发现" + count + "个更新",
				    ImageUtilities.loadImageIcon("resource/gourd16.png", false),
				    "单击此处可将您的应用程序更新为最新",
				    new com.acuilab.bc.main.action.CheckForUpdatesAction()
			    );
			}
		    }

		}, 1*1000);	// 延迟5秒执行，避免
                
                // TODO: 从https://moonswap.fi/wallet获得价格
                Browser browser = new Browser(BrowserType.LIGHTWEIGHT, new BrowserContext(new BrowserContextParams(Files.createTempDir().getAbsolutePath())));
//        	view = new BrowserView(browser);
//        	this.add(view, BorderLayout.CENTER);

                browser.loadURL("https://moonswap.fi/wallet");
                browser.addLoadListener(new LoadAdapter() {
                    @Override
                    public void onFinishLoadingFrame(FinishLoadingEvent event) {
                        if (event.isMainFrame()) {
                            System.out.println("_____________________Main frame has finished loading_____________________");
                            System.out.println(browser.getHTML());
                        }
                    }
                });
            }
        });
    }

    private void initTables() {
	// 
	try {
	    try (Statement stmt = getConnection().createStatement()) {
		stmt.execute("SELECT 1 FROM wallet");
	    }
	} catch(SQLException e) {
	    try {
		try (Statement stmt = getConnection().createStatement()) {
		    stmt.execute("CREATE TABLE wallet (wname VARCHAR(255), pwdMd5 VARCHAR(255), blockChainSymbol VARCHAR(255), waddress VARCHAR(255), privateKeyAES VARCHAR(255), mnemonicAES VARCHAR(255), created TIMESTAMP, PRIMARY KEY(wname))");
                }
	    } catch(SQLException ex) {
		Exceptions.printStackTrace(ex);
	    }
	}
	
	// conflux CIP37新地址升级
	try {
	    try (Statement stmt = getConnection().createStatement()) {
                CFXExtend cfxExtend = Lookup.getDefault().lookup(CFXExtend.class);
		ResultSet rs = stmt.executeQuery("select wname, waddress from wallet where blockChainSymbol='" + Constants.CFX_BLOCKCHAIN_SYMBAL + "'");
		while(rs.next()) {
		    String name = rs.getString("wname");
		    String address = rs.getString("waddress");
		    if(StringUtils.startsWith(address, "0x1")) {
			try (Statement stmt2 = getConnection().createStatement()) {
			    stmt2.execute("UPDATE wallet set waddress='" + cfxExtend.convertAddress(address) + "' where wname='" + name + "'");
			}
		    }
		}
		rs.close();
	    }
	} catch (SQLException ex) {
	    Exceptions.printStackTrace(ex);
	}
        
	try {
	    try (Statement stmt = getConnection().createStatement()) {
		stmt.execute("SELECT 1 FROM addressBook");
	    }
	} catch(SQLException e) {
	    try {
		try (Statement stmt = getConnection().createStatement()) {
                    stmt.execute("CREATE TABLE addressBook (id VARCHAR(255), address VARCHAR(255), remark VARCHAR(255), blockChainSymbol VARCHAR(255), created TIMESTAMP, PRIMARY KEY(id))");
                }
	    } catch(SQLException ex) {
		Exceptions.printStackTrace(ex);
	    }
	}
    }

    @Override
    public void close() {
        BlockChainManager.getDefault().close();
        
        // jxBrowser dispose
	for (JxBrowserDisposer disposer : Lookup.getDefault().lookupAll(JxBrowserDisposer.class)) {
	    disposer.disposeBrowser();
	}
//        WelcomeTopComponent welcomeTC = (WelcomeTopComponent)WindowManager.getDefault().findTopComponent("WelcomeTopComponent");
//        if(welcomeTC != null) {
//            welcomeTC.disposeBrowser();
//        }
        
        if(Environment.isMac()) {
            LOG.log(Level.INFO, "BrowserCore.shutdown thread name: {0}", Thread.currentThread().getName());
            BrowserCore.shutdown();
            LOG.log(Level.INFO, "BrowserCore.shutdown completed");
        }
    }

    @Override
    public boolean closing() {
//        NotifyDescriptor descriptor = new NotifyDescriptor.Confirmation(
//                "您真的要退出该应用程序吗？",
//                "退出",
//                NotifyDescriptor.YES_NO_OPTION);
//        Object retval = DialogDisplayer.getDefault().notify(descriptor);
//        return retval.equals(NotifyDescriptor.YES_OPTION);
        ConfirmDialog dlg = new ConfirmDialog(null, "退出", "您真的要退出该应用程序吗？");
        dlg.setVisible(true);
        return dlg.getReturnStatus() == ConfirmDialog.RET_OK;
    }
    
    
    
    public static Connection getConnection() throws SQLException {
	if(conn == null || conn.isClosed()) {
	    conn = DriverManager.getConnection("jdbc:derby:acuibc;create=true");
	}
	
	return conn;
    }
}
