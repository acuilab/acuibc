package com.acuilab.bc.main;

import com.acuilab.bc.main.manager.BlockChainManager;
import com.acuilab.bc.main.manager.CoinManager;
import com.acuilab.bc.main.manager.DAppManager;
import com.acuilab.bc.main.manager.NFTManager;
import com.acuilab.bc.main.ui.ConfirmDialog;
import com.acuilab.bc.main.util.Constants;
import com.acuilab.bc.main.util.Utils;
import com.acuilab.bc.main.welcome.WelcomeTopComponent;
import com.teamdev.jxbrowser.chromium.BrowserCore;
import com.teamdev.jxbrowser.chromium.be;
import com.teamdev.jxbrowser.chromium.internal.Environment;
import java.io.File;
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
import java.util.prefs.Preferences;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.apache.commons.lang3.StringUtils;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall {
    
    private static final Logger LOG = Logger.getLogger(Installer.class.getName());
    private static Connection conn = null;
    
    // 自动更新检查时间间隔设置为"每次启动时"
    // @see org.netbeans.modules.autoupdate.ui.actions.AutoupdateSettings
    static {
        Preferences pref = NbPreferences.root ().node ("/org/netbeans/modules/autoupdate");
        if(pref.getInt("period", 0) != 0) {
            pref.putInt("period", 0);
        }
    }
    
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
		ResultSet rs = stmt.executeQuery("select wname, waddress from wallet where blockChainSymbol='" + Constants.CFX_BLOCKCHAIN_SYMBAL + "'");
		while(rs.next()) {
		    String name = rs.getString("wname");
		    String address = rs.getString("waddress");
		    if(StringUtils.startsWith(address, "0x1")) {
			BlockChain bc = BlockChainManager.getDefault().getBlockChain(Constants.CFX_BLOCKCHAIN_SYMBAL);
			try (Statement stmt2 = getConnection().createStatement()) {
			    stmt2.execute("UPDATE wallet set waddress='" + bc.convertAddress(address) + "' where wname='" + name + "'");
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
        WelcomeTopComponent welcomeTC = (WelcomeTopComponent)WindowManager.getDefault().findTopComponent("WelcomeTopComponent");
        if(welcomeTC != null) {
            welcomeTC.disposeBrowser();
        }
        
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
