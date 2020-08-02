package com.acuilab.bc.main.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * 操作数据库的公共dao
 *
 * @author dcl
 */
public class JDBCUtil {

    public static final Logger LOG = Logger.getLogger(JDBCUtil.class.getName());

    public static <T> List<T> executeQuery(String sql, Object[] params, Connection conn, Mapper<T> mapper) throws SQLException {
	//判断参数列表是否为空	    
	try (PreparedStatement ps = conn.prepareStatement(sql)) {
	    //判断参数列表是否为空
	    if (params != null) {
		for (int i = 0; i < params.length; i++) {
		    //设置占位符  
		    ps.setObject(i + 1, params[i]);
		}
	    }
	    try (ResultSet rs = ps.executeQuery()) {
		return mapper.doMap(rs);
	    }
	}
    }
    
    public static <T> T executeQuery(String sql, Object[] params, Connection conn, MapperUnique<T> mapper) throws SQLException {
	//判断参数列表是否为空	    
	try (PreparedStatement ps = conn.prepareStatement(sql)) {
	    //判断参数列表是否为空
	    if (params != null) {
		for (int i = 0; i < params.length; i++) {
		    //设置占位符  
		    ps.setObject(i + 1, params[i]);
		}
	    }
	    try (ResultSet rs = ps.executeQuery()) {
		return mapper.doMap(rs);
	    }
	}
    }

    /* 
     * 增加删除改 
     */
    public static int executeUpdate(String sql, Object[] params, Connection conn) throws SQLException {
	//判断参数列表是否为空
	try ( //获取PreparedStatement对象，通过conn
		PreparedStatement ps = conn.prepareStatement(sql)) {
	    //判断参数列表是否为空
	    if (params != null) {
		//设置占位符
		for (int i = 0; i < params.length; i++) {
		    ps.setObject(i + 1, params[i]);
		}

	    }
	    //执行，增，删除，修改
	    return ps.executeUpdate();
	}
    }
    
    public static java.sql.Timestamp getTimeStamp(java.util.Date date) {
        if(date != null) {
            return new java.sql.Timestamp(date.getTime());
        }
        
        return null;
    }

    public static abstract class Mapper<T> {

	public List<T> doMap(ResultSet rs) throws SQLException {
	    List<T> list = new ArrayList<>();
	    while (rs.next()) {
		T t = next(rs);
		if (t != null) {
		    list.add(t);
		}
	    }

	    return list;
	}

	protected abstract T next(ResultSet rs) throws SQLException;
    }
    
    public static abstract class MapperUnique<T> {

	public T doMap(ResultSet rs) throws SQLException {
	    if (rs.next()) {
		return next(rs);
	    }

	    return null;
	}

	protected abstract T next(ResultSet rs) throws SQLException;
    }
}
