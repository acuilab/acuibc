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
public class CWITCoin extends ERC20Coin {
    
    private static final Logger LOG = Logger.getLogger(CWITCoin.class.getName());

    public static final String CONTRACT_ADDRESS = "cfx:ace2nh5arcb4nxhkh5xdtac087rc7a9hgam96t8j6j";

    public static final String NAME = "conflux WIT coin";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSymbol() {
        return Constants.CFX_CWIT_SYMBOL;
    }

    @Override
    public String getBlockChainSymbol() {
        return Constants.CFX_BLOCKCHAIN_SYMBAL;
    }
    
    @Override
    public Icon getIcon(int size) {
        return ImageUtilities.loadImageIcon("/resource/cwit" + size + ".png", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/cwit" + size + ".png", true);
    }

    @Override
    public String getMainUnit() {
        return "cWIT";
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
