package com.acuilab.bc.main.dao;

import com.acuilab.bc.main.Installer;
import com.acuilab.bc.main.util.JDBCUtil;
import com.acuilab.bc.main.util.JDBCUtil.Mapper;
import com.acuilab.bc.main.util.JDBCUtil.MapperUnique;
import com.acuilab.bc.main.wallet.Address;
import com.acuilab.bc.main.wallet.Wallet;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 *
 * @author admin
 */
public class AddressBookDAO {
    private AddressBookDAO() {}
    
    public static void insert(Address address) throws SQLException {
        try (PreparedStatement ps = Installer.getConnection().prepareStatement("insert into addressBook (id, address, remark, blockChainSymbol, created) values (?, ?, ?, ?, ?)")) {
            ps.setString(1, address.getId());
            ps.setString(2, address.getAddress());
            ps.setString(3, address.getRemark());
            ps.setString(4, address.getBlockChainSymbol());
            ps.setTimestamp(5, JDBCUtil.getTimeStamp(address.getCreated()));
            
            ps.executeUpdate();
        }
    }
    
//    public static Wallet getByName(String name) throws SQLException {
//	List<Wallet> list =  JDBCUtil.executeQuery("select wname, pwdMd5, blockChainSymbol, waddress, privateKeyAES, mnemonicAES, created from wallet where wname=?", new Object[] {name}, Installer.getConnection(), new Mapper<Wallet>() {
//	    @Override
//	    protected Wallet next(ResultSet rs) throws SQLException {
//		return new Wallet(rs.getString("wname"), rs.getString("pwdMd5"), rs.getString("blockChainSymbol"), rs.getString("waddress"), rs.getString("privateKeyAES"), rs.getString("mnemonicAES"), rs.getDate("created"));
//	    }
//	});
//        
//        return list.isEmpty() ? null : list.get(0);
//    }
    
    /**
     * 指定名称的钱包是否存在
     * @return
     * @throws SQLException 
     */
    public static boolean existByAddress(String address) throws SQLException {
        Integer exist = JDBCUtil.executeQuery("select 1 from addressBook where address=? OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY", new Object[] {address}, Installer.getConnection(), new MapperUnique<Integer>() {
	    @Override
	    protected Integer next(ResultSet rs) throws SQLException {
		return rs.getInt(1);
	    }
	});
        
        return exist != null;
    }
    
    public static void delete(String id) throws SQLException {
        JDBCUtil.executeUpdate("delete from addressBook where id=?", new Object[] {id}, Installer.getConnection());
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
     * 获得地址数量
     * @return
     * @throws SQLException 
     */
    public static int getCount() throws SQLException {
	return JDBCUtil.executeQuery("select count(1) from addressBook", new Object[] {}, Installer.getConnection(), new MapperUnique<Integer>() {
	    @Override
	    protected Integer next(ResultSet rs) throws SQLException {
		return rs.getInt(1);
	    }
	    
	});
    }
    
    public static List<Address> getList() throws SQLException {
	return JDBCUtil.executeQuery("select id, address, remark, blockChainSymbol, created from addressBook order by address", new Object[] {}, Installer.getConnection(), new Mapper<Address>() {
	    @Override
	    protected Address next(ResultSet rs) throws SQLException {
                Address address = new Address();
                address.setId(rs.getString("id"));
                address.setAddress(rs.getString("address"));
                address.setRemark(rs.getString("remark"));
                address.setBlockChainSymbol(rs.getString("blockChainSymbol"));
                address.setCreated(rs.getDate("created"));
                
                return address;
	    }
	    
	});
    }
    
    public static List<Address> getListByBlockChainSymbol(String blockChainSymbol) throws SQLException {
	return JDBCUtil.executeQuery("select id, address, remark, blockChainSymbol, created from addressBook where blockChainSymbol=? order by address", new Object[] {blockChainSymbol}, Installer.getConnection(), new Mapper<Address>() {
	    @Override
	    protected Address next(ResultSet rs) throws SQLException {
                Address address = new Address();
                address.setId(rs.getString("id"));
                address.setAddress(rs.getString("address"));
                address.setRemark(rs.getString("remark"));
                address.setBlockChainSymbol(rs.getString("blockChainSymbol"));
                address.setCreated(rs.getDate("created"));
                
                return address;
	    }
	    
	});
    }
    
    public static void update(Address address) throws SQLException {
        JDBCUtil.executeUpdate("update addressBook set address=?, remark=?, blockChainSymbol=? where id=?", new Object[] {address.getAddress(), address.getRemark(), address.getBlockChainSymbol(), address.getId()}, Installer.getConnection());
    }
}
