package com.acuilab.bc.cfx;

import conflux.web3j.CfxUnit;
import java.awt.Image;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.logging.Logger;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
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
    public Icon getIcon(int size) {
        return ImageUtilities.loadImageIcon("/resource/cpi" + size + ".png", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/cpi" + size + ".png", true);
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
    public String getContractAddress() {
        return CONTRACT_ADDRESS;
    }

    @Override
    public boolean isDivisible() {
        return false;
    }
}
