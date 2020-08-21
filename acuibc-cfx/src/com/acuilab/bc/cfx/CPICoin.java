package com.acuilab.bc.cfx;

import conflux.web3j.CfxUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.logging.Logger;
import org.openide.util.Lookup;

/**
 *
 * @author admin
 */
public class CPICoin extends ERC20Coin {
    
    private static final Logger LOG = Logger.getLogger(CPICoin.class.getName());

    public static final String CONTRACT_ADDRESS = "0x8f50e31a4e3201b2f7aa720b3754dfa585b4dbfa";

    public static final String NAME = "CPI Coin";
    public static final String SYMBOL = "CPI";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }

    @Override
    public String getBlockChainSymbol() {
        return CFXBlockChain.SYMBOL;
    }

    @Override
    public String getMainUnit() {
        return "CPI";
    }

    @Override
    public String getMinUnit() {
        return "CPI";  // 没有最小单位
    }

    @Override
    public BigDecimal minUnit2MainUint(BigInteger minUnitValue) {
        return new BigDecimal(minUnitValue);
    }

    @Override
    public BigInteger mainUint2MinUint(double mainUnitValue) {
        return BigDecimal.valueOf(mainUnitValue).toBigIntegerExact();
    }

    @Override
    public BigInteger mainUint2MinUint(long mainUnitValue) {
        return BigInteger.valueOf(mainUnitValue);
    }

    @Override
    public int getMainUnitScale() {
        return 0;
    }

    @Override
    public int getScale() {
        return 0;
    }

    @Override
    public int gasMin() {
        return CfxUnit.DEFAULT_GAS_LIMIT.intValue();
    }

    @Override
    public int gasMax() {
        // @see http://acuilab.com:8080/articles/2020/08/12/1597238136717.html
        return (int) (CfxUnit.DEFAULT_GAS_LIMIT.intValue() * 1.3);  // 向下取整
    }

    @Override
    public int gasDefaultValue() {
        return CfxUnit.DEFAULT_GAS_LIMIT.intValue();
    }

    @Override
    public String gasDesc(int gas) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        BigInteger gasValue = bc.getGasPrice().multiply(BigInteger.valueOf(gas));
        return gasValue + " drip/" + CfxUnit.drip2Cfx(gasValue).toPlainString() + " CFX";
    }

    @Override
    public String getContractAddress() {
        return CONTRACT_ADDRESS;
    }
}
