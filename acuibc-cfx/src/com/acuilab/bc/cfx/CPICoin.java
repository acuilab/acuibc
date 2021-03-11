package com.acuilab.bc.cfx;

import com.acuilab.bc.main.util.Constants;
import java.awt.Image;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.logging.Logger;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;

/**
 *
 * @author admin
 */
public class CPICoin extends ERC20Coin {
    
    private static final Logger LOG = Logger.getLogger(CPICoin.class.getName());

    public static final String CONTRACT_ADDRESS = "cfx:achzb224k23adp11zk3a0r4y58w2nrg59je6ckjkee";

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
        return Constants.CFX_BLOCKCHAIN_SYMBAL;
    }
    
    @Override
    public Icon getIcon(int size) {
        return ImageUtilities.loadImageIcon("/resource/CPI" + size + ".png", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/CPI" + size + ".png", true);
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
