package com.acuilab.bc.cfx;

import com.acuilab.bc.main.coin.IFCExchange;
import conflux.web3j.Cfx;
import java.util.logging.Logger;
import org.openide.util.Lookup;

/**
 *
 * @author acuilab.com
 */
public class FCExchange implements IFCExchange {
    
    private static final Logger LOG = Logger.getLogger(CUSDCCoin.class.getName());

    public static final String CONTRACT_ADDRESS = "cfx:acdrd6ahf4fmdj6rgw4n9k4wdxrzfe6ex6jc7pw50m";

    @Override
    public UserInfo userInfos() {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        return null;
    }

}
