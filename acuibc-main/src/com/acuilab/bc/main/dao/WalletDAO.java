package com.acuilab.bc.main.dao;

import com.acuilab.bc.main.Installer;
import com.acuilab.bc.main.util.JDBCUtil;
import com.acuilab.bc.main.util.JDBCUtil.Mapper;
import com.acuilab.bc.main.util.JDBCUtil.MapperUnique;
import com.acuilab.bc.main.wallet.Wallet;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 *
 * @author admin
 */
public class WalletDAO {
    private WalletDAO() {}
    
    public static void insert(Wallet wallet) throws SQLException {
        try (PreparedStatement ps = Installer.getConnection().prepareStatement("insert into wallet (wname, pwdMd5, blockChainSymbol, waddress, privateKeyAES, mnemonicAES, created) values (?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, wallet.getName());
            ps.setString(2, wallet.getPwdMD5());
            ps.setString(3, wallet.getBlockChainSymbol());
            ps.setString(4, wallet.getAddress());
            ps.setString(5, wallet.getPrivateKeyAES());
	    ps.setString(6, wallet.getMnemonicAES());
            ps.setTimestamp(7, JDBCUtil.getTimeStamp(wallet.getCreated()));
            
            ps.executeUpdate();
        }
    }
    
    public static Wallet getByName(String name) throws SQLException {
	List<Wallet> list =  JDBCUtil.executeQuery("select wname, pwdMd5, blockChainSymbol, waddress, privateKeyAES, mnemonicAES, created from wallet where wname=?", new Object[] {name}, Installer.getConnection(), new Mapper<Wallet>() {
	    @Override
	    protected Wallet next(ResultSet rs) throws SQLException {
		return new Wallet(rs.getString("wname"), rs.getString("pwdMd5"), rs.getString("blockChainSymbol"), rs.getString("waddress"), rs.getString("privateKeyAES"), rs.getString("mnemonicAES"), rs.getDate("created"));
	    }
	});
        
        return list.isEmpty() ? null : list.get(0);
    }
    
    /**
     * 指定名称的钱包是否存在
     * @return
     * @throws SQLException 
     */
    public static boolean existByName(String name) throws SQLException {
        Integer exist = JDBCUtil.executeQuery("select 1 from wallet where wname=? OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY", new Object[] {name}, Installer.getConnection(), new MapperUnique<Integer>() {
	    @Override
	    protected Integer next(ResultSet rs) throws SQLException {
		return rs.getInt(1);
	    }
	});
        
        return exist != null;
    }
    
    public static void delete(String name) throws SQLException {
        JDBCUtil.executeUpdate("delete from wallet where wname=?", new Object[] {name}, Installer.getConnection());
    }
    
//    public static boolean isPwdMatch(String name, String pwdMD5) throws SQLException {
//        Integer exist = JDBCUtil.executeQuery("select 1 from wallet where wname=? and pwdMd5=? OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY", new Object[] {name, pwdMD5}, Installer.getConnection(), new MapperUnique<Integer>() {
//	    @Override
//	    protected Integer next(ResultSet rs) throws SQLException {
//		return rs.getInt(1);
//	    }
//	});
//        
//        return exist != null;
//    }
    
    /**
     * 获得钱包数量
     * @return
     * @throws SQLException 
     */
    public static int getCount() throws SQLException {
	return JDBCUtil.executeQuery("select count(1) from wallet", new Object[] {}, Installer.getConnection(), new MapperUnique<Integer>() {
	    @Override
	    protected Integer next(ResultSet rs) throws SQLException {
		return rs.getInt(1);
	    }
	    
	});
    }
    
    public static List<Wallet> getList() throws SQLException {
        // 按blockChainSymbol及钱包名称排序
	return JDBCUtil.executeQuery("select wname, pwdMd5, blockChainSymbol, waddress, privateKeyAES, mnemonicAES, created from wallet order by blockChainSymbol, wname", new Object[] {}, Installer.getConnection(), new Mapper<Wallet>() {
	    @Override
	    protected Wallet next(ResultSet rs) throws SQLException {
		return new Wallet(rs.getString("wname"), rs.getString("pwdMd5"), rs.getString("blockChainSymbol"), rs.getString("waddress"), rs.getString("privateKeyAES"), rs.getString("mnemonicAES"), rs.getDate("created"));
	    }
	    
	});
    }
    
    public static List<Wallet> getListByBlockChainSymbol(String blockChainSymbol) throws SQLException {
        // 按blockChainSymbol及钱包名称排序
	return JDBCUtil.executeQuery("select wname, pwdMd5, blockChainSymbol, waddress, privateKeyAES, mnemonicAES, created from wallet where blockChainSymbol=? order by wname", new Object[] {blockChainSymbol}, Installer.getConnection(), new Mapper<Wallet>() {
	    @Override
	    protected Wallet next(ResultSet rs) throws SQLException {
		return new Wallet(rs.getString("wname"), rs.getString("pwdMd5"), rs.getString("blockChainSymbol"), rs.getString("waddress"), rs.getString("privateKeyAES"), rs.getString("mnemonicAES"), rs.getDate("created"));
	    }
	    
	});
    }
    
    public static void updatePwd(String name, String pwdMD5, String mnemonicAES, String privateKeyAES) throws SQLException {
        JDBCUtil.executeUpdate("update wallet set pwdMd5=?, mnemonicAES=?, privateKeyAES=? where wname=?", new Object[] {pwdMD5, mnemonicAES, privateKeyAES, name}, Installer.getConnection());
    }
    
    public static void updateName(String name, String newName) throws SQLException {
        JDBCUtil.executeUpdate("update wallet set wname=? where wname=?", new Object[] {newName, name}, Installer.getConnection());
    }
}
