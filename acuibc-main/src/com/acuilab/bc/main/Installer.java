package com.acuilab.bc.main;

import com.acuilab.bc.main.manager.BlockChainManager;
import com.acuilab.bc.main.manager.CoinManager;
import com.acuilab.bc.main.ui.ConfirmDialog;
import com.acuilab.bc.main.util.Constants;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall {
    
    private static final Logger LOG = Logger.getLogger(Installer.class.getName());
    private static Connection conn = null;

    @Override
    public void restored() {
        // 在配置文件gourd.conf中指定
//        System.setProperty("sun.java2d.noddraw", "true");
        
        // java.lang.IllegalArgumentException: Renderer extends the SubstanceDefaultTableCellRenderer but does not return one in its getTableCellRendererComponent() method
//        System.setProperty("insubstantial.looseTableCellRenderers", "true");
//        System.setProperty("insubstantial.checkEDT", "false");
//        System.setProperty("insubstantial.logEDT", "false");


        
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
		// 最大化显示；cuizhf, 20200916，不强制全屏
		WindowManager.getDefault().getMainWindow().setExtendedState(JFrame.MAXIMIZED_BOTH);
                WindowManager.getDefault().getMainWindow().setLocationRelativeTo(null);
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
