package com.acuilab.bc.cfx;

import static com.acuilab.bc.cfx.CFXCoin.STAKING_CONTRACT_ADDRESS;
import com.acuilab.bc.main.coin.IFCExchange;
import conflux.web3j.Account;
import conflux.web3j.Cfx;
import conflux.web3j.types.Address;
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
    public UserInfo userInfos(String privateKey) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        Account account = Account.create(cfx, privateKey);
        return account.call(new Address(CONTRACT_ADDRESS), "withdraw", new org.web3j.abi.datatypes.Uint(value));
    }

}
