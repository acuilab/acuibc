package com.acuilab.bc.main.coin;

import com.acuilab.bc.main.wallet.TransferRecord;
import com.acuilab.bc.main.wallet.Wallet;
import java.awt.Image;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import javax.swing.Icon;

/**
 *oo
 * @author admin
 */
public interface ICoin {
    
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
     * 获得图标
     * @param size
     * @return 
     */
    Icon getIcon(int size);
    
    Image getIconImage(int size);
    
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
     * 
     * @deprecated 文本转成doube，可能会有精度损失；建议使用BigInteger mainUint2MinUint(BigInteger mainUnitValue);
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
    
//    // 类型: BASE、TOKEN、...
//    public Type getType();
    
    // 返回某个地址的账户余额
    public BigInteger balanceOf(String address) throws Exception;
    
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
     * 批量转账
     * @param privateKey    私钥
     * @param tos        接收地址列表
     * @param values     转账数量列表
     * @param gas       矿工费
     * @param callback
     * @throws java.lang.Exception
     */
    public void batchTransfer(String privateKey, String[] tos, BigInteger[] values, BigInteger gas, BatchTransferCallback callback) throws Exception;

    /**
     * 根据某个地址获得交易记录
     * @param wallet
     * @param coin
     * @param address
     * @param limit     最多同步记录数
     * @return 
     * @throws java.lang.Exception 
     */
    public List<TransferRecord> getTransferRecords(Wallet wallet, ICoin coin, String address, int limit) throws Exception;
    
    
    /**
     * 矿工费单价最小值
     * @return 
     */
    int gasMin();
    
    /**
     * 矿工费单价最大值
     * @return 
     */
    int gasMax();
    
    /**
     * 矿工费单价初值
     * @return 
     */
    int gasDefault();
    
    /**
     * 矿工费描述：例如1drip
     * @param gas
     * @return 
     */
    String gasDesc(int gas);
    
    int gasLimit();
    
    /**
     * 是否可分割
     * @return 
     */
    boolean isDivisible();
    
    /**
     * 获得合约地址，非主网币需覆盖此方法以提供合约地址
     * @return 
     */
    default String getContractAddress() {
        return null;
    }
    
    /**
     * 主网币的合约地址为空，此方法子类不需要覆盖
     * @return 
     */
    default boolean isBaseCoin() {
        return getContractAddress() == null;
    }
    
    /**
     * 将gas转换为最小单位（以太坊的gas单位太贵，一般以gwei为单位）
     * @param gas
     * @return 
     */
    default BigInteger gas2MinUnit(long gas) {
	return BigInteger.valueOf(gas);
    }
    
    /**
     * 主单位转换为最小单位
     * @param mainUnitValue
     * @return 
     * 
     */
    default BigInteger mainUint2MinUint(BigDecimal mainUnitValue) {
	return mainUnitValue.multiply(new BigDecimal(BigInteger.TEN.pow(getScale()))).toBigIntegerExact();
    }
    
    interface BatchTransferCallback {
	/**
	 * 
	 * @param address   转账地址
	 * @param hash	    交易哈希
	 * @param index	    当前转账第几个
	 * @param size     一共需要转账数量
	 * @return	    是否停止转账
	 */
	boolean transferFinished(String address, String hash, int index, int size);
    }
}
