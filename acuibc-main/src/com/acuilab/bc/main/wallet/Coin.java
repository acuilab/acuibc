package com.acuilab.bc.main.wallet;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 *oo
 * @author admin
 */
public interface Coin {
    
    public void init();
    
    // 名称
    public String getName();
    
    // 简称（外键）
    public String getSymbol();
    
    /**
     * 获得区块链简称
     * @return 
     */
    public String getBlockChainSymbol();
    
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
     * 根据某个地址获得交易记录
     * @param wallet
     * @param coin
     * @param address
     * @param limit     最多同步记录数
     * @return 
     * @throws java.lang.Exception 
     */
    public List<TransferRecord> getTransferRecords(Wallet wallet, Coin coin, String address, int limit) throws Exception;
    
    
    /**
     * 矿工费最小值
     * @param address
     * @return 
     */
    int gasMin(String address);
    
    /**
     * 矿工费最大值
     * @param address
     * @return 
     */
    int gasMax(String address);
    
    /**
     * 矿工费初值
     * @param address
     * @return 
     */
    int gasDefaultValue(String address);
    
    /**
     * 矿工费描述：例如20 Gdrip/0.002 CFX
     * @param gas
     * @return 
     */
    String gasDesc(int gas);
    
    public static enum Type {
        // 主网币、代币
        BASE, TOKEN
    }
    
}
