package com.acuilab.bc.cfx;

import com.acuilab.bc.main.util.Constants;
import conflux.web3j.CfxUnit;
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
public class HydraSFCoin extends ERC20Coin {
    
    private static final Logger LOG = Logger.getLogger(HydraSFCoin.class.getName());

    public static final String CONTRACT_ADDRESS = "cfx:acfm2ej3uxez2n9b2msdzsrzmfbyfk4h9an8hcrxzf";

    public static final String NAME = "HydraSF Coin";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSymbol() {
        return Constants.CFX_HydraSF_SYMBOL;
    }

    @Override
    public String getBlockChainSymbol() {
        return Constants.CFX_BLOCKCHAIN_SYMBAL;
    }
    
    @Override
    public Icon getIcon(int size) {
        return ImageUtilities.loadImageIcon("/resource/HydraSF" + size + ".png", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/HydraSF" + size + ".png", true);
    }

    @Override
    public String getMainUnit() {
        return "HydraSF";
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
    public String getContractAddress() {
        return CONTRACT_ADDRESS;
    }

    @Override
    public boolean isDivisible() {
        return true;
    }
}
