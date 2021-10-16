package com.acuilab.bc.cfx.tinyversus;

import com.acuilab.bc.cfx.CFXBlockChain;
import conflux.web3j.Cfx;
import conflux.web3j.types.Address;
import org.openide.util.Lookup;
import com.acuilab.bc.main.cfx.dapp.tinyversus.ILumiContract;
import conflux.web3j.Account;

/**
 *
 * @author acuilab.com
 */
public class LumiContract implements ILumiContract {
    
    public static final String LUMI_CONTRACT = "cfx:aceaujgc395uru3ds1nbrp78szxtxd82xaf82spjd2";
    
    @Override
    public String withdrawPoolAllV2(String privateKey) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        Account account = Account.create(cfx, privateKey);
        return account.call(new Address(LUMI_CONTRACT), "withdrawPoolAllV2");
    }
}
