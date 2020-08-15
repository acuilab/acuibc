package com.acuilab.bc.cfx;

import com.acuilab.bc.main.wallet.Coin;
import com.acuilab.bc.main.wallet.TransferRecord;
import com.acuilab.bc.main.wallet.Wallet;
import com.google.common.collect.Lists;
import conflux.web3j.CfxUnit;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 *
 * @author admin
 */
public class FCCoin implements Coin {
    
    public static final String NAME = "Fans Coin";
    public static final String SYMBOL = "FC";

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
        return "FC";
    }

    @Override
    public String getMinUnit() {
        return "";  // 没有最小单位
    }

    @Override
    public BigDecimal minUnit2MainUint(BigInteger minUnitValue) {
        return CfxUnit.drip2Cfx(minUnitValue);
    }

    @Override
    public BigInteger mainUint2MinUint(double mainUnitValue) {
        return CfxUnit.cfx2Drip(mainUnitValue);
    }

    @Override
    public BigInteger mainUint2MinUint(long mainUnitValue) {
        return CfxUnit.cfx2Drip(mainUnitValue);
    }

    @Override
    public int getMainUnitScale() {
        return 6;
    }

    @Override
    public int getScale() {
        return 18;
    }

    @Override
    public Type getType() {
        return Type.TOKEN;
    }

    @Override
    public BigInteger balanceOf(String address) {
        return BigInteger.ZERO;
    }

    @Override
    public String transfer(String privateKey, String to, BigInteger value, BigInteger gas) throws Exception {
        return "";
    }

    @Override
    public List<TransferRecord> getTransferRecords(Wallet wallet, Coin coin, String address, int limit) throws Exception {
        return Lists.newArrayList();
    }

}
