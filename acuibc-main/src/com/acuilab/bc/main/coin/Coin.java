package com.acuilab.bc.main.coin;

import com.acuilab.bc.main.wallet.TransferRecord;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 *
 * @author admin
 */
public interface Coin {
    // 名称
    public String getName();
    
    // 简称
    public String getSymbol();
    
    /**
     * 主单位；例如CFX
     * @return 
     */
    String getMainUnit();
    
    /**
     * 获得最小单位：例如drip
     * @return 
     */
    String getMinUnit();
    
    /**
     * 最小单位转换为主单位
     * @param minUnitValue
     * @return 
     */
    BigDecimal minUnit2MainUint(BigInteger minUnitValue);
    
    /**
     * 主单位转换为最小单位
     * @param mainUnitValue
     * @return 
     */
    BigInteger mainUint2MinUint(double mainUnitValue);
    
    /**
     * 主单位转换为最小单位
     * @param mainUnitValue
     * @return 
     */
    BigInteger mainUint2MinUint(long mainUnitValue);
    
    /**
     * 主单位小数位数，超出则四舍五入
     * @return 
     */
    int getMainUnitScale();
    
    /**
     * 小数精度，例如FC的小数精度是18
     * @return 
     */
    int getScale();
    
    // 类型: BASE、TOKEN、...
    public Type getType();
    
    // 返回某个地址的账户余额
    public BigInteger balanceOf(String address);
    
    /**
     * 转账
     * @param privateKey    私钥
     * @param to        接收地址
     * @param value     转账数量
     * @param gas       矿工费
     * @return 
     * @throws java.lang.Exception
     */
    public String transfer(String privateKey, String to, BigInteger value, BigInteger gas) throws Exception;
    
    /**
     * 交易记录
     * @param address
     * @return 
     */
    public List<TransferRecord> transferRecord(String address);
    
    public static enum Type {
        // 主网币、代币
        BASE, TOKEN
    }
}
