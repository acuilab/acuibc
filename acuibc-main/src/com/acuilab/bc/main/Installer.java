package com.acuilab.bc.main;

import com.acuilab.bc.main.manager.BlockChainManager;
import com.acuilab.bc.main.manager.CoinManager;
import com.acuilab.bc.main.util.Constants;
import java.awt.PopupMenu;
import java.awt.event.MouseListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ToolbarPool;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall {
    
    private static final Logger LOG = Logger.getLogger(Installer.class.getName());
    private static Connection conn = null;

    @Override
    public void restored() {
        System.setProperty("sun.java2d.noddraw", "true");
        
        // java.lang.IllegalArgumentException: Renderer extends the SubstanceDefaultTableCellRenderer but does not return one in its getTableCellRendererComponent() method
        System.setProperty("insubstantial.looseTableCellRenderers", "true");
        System.setProperty("insubstantial.checkEDT", "false");
        System.setProperty("insubstantial.logEDT", "false");
        
	// derby
	System.setProperty("derby.system.home", System.getProperty("netbeans.user", System.getProperty("user.home")) + File.separator + "databases");
	LOG.log(Level.INFO, "derby.system.home={0}", System.getProperty("derby.system.home"));
	initTables();
        
        // init
        BlockChainManager.getDefault();
        CoinManager.getDefault();
        
        // 设置主窗口标题
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
		// 设置标题
                WindowManager.getDefault().getMainWindow().setTitle(Constants.TITLE);
		// 最大化显示
		WindowManager.getDefault().getMainWindow().setExtendedState(JFrame.MAXIMIZED_BOTH);
                
                MouseListener[] listeners = ToolbarPool.getDefault().getMouseListeners();
                System.out.println("listeners.size=========================" + listeners.length);
                for(MouseListener listener : listeners) {
                    ToolbarPool.getDefault().removeMouseListener(listener);
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
//        
//	try {
//	    try (Statement stmt = getConnection().createStatement()) {
//		stmt.execute("SELECT 1 FROM transferRecord");
//	    }
//	} catch(SQLException e) {
//	    try {
//		try (Statement stmt = getConnection().createStatement()) {
//                    stmt.execute("CREATE TABLE transferRecord (coinName VARCHAR(255), status VARCHAR(255), sendAddress VARCHAR(255), recvAddress VARCHAR(255), remark VARCHAR(255), hash VARCHAR(255), created TIMESTAMP, PRIMARY KEY(hash))");
//                }
//	    } catch(SQLException ex) {
//		Exceptions.printStackTrace(ex);
//	    }
//	}
    }

    @Override
    public void close() {
        BlockChainManager.getDefault().close();
    }

    @Override
    public boolean closing() {
        NotifyDescriptor descriptor = new NotifyDescriptor.Confirmation(
                "您真的要退出该应用程序吗？",
                "退出",
                NotifyDescriptor.YES_NO_OPTION);
        Object retval = DialogDisplayer.getDefault().notify(descriptor);
        return retval.equals(NotifyDescriptor.YES_OPTION);
    }
    
    
    
    public static Connection getConnection() throws SQLException {
	if(conn == null || conn.isClosed()) {
	    conn = DriverManager.getConnection("jdbc:derby:acuibc;create=true");
	}
	
	return conn;
    }
}
