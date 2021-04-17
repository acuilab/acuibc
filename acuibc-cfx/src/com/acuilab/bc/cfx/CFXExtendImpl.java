package com.acuilab.bc.cfx;

import com.acuilab.bc.main.cfx.CFXExtend;
import conflux.web3j.Account;
import conflux.web3j.Cfx;
import conflux.web3j.types.Address;
import conflux.web3j.types.RawTransaction;
import java.math.BigInteger;
import org.apache.commons.lang3.StringUtils;
import org.openide.util.Lookup;

/**
 *
 * @author acuilab.com
 */
public class CFXExtendImpl implements CFXExtend {

    @Override
    public String send(String privateKey, String from, BigInteger gas, String to, BigInteger value, BigInteger storageLimit, String data) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        try {
        Account account = Account.create(cfx, privateKey);
	BigInteger nonce = account.getNonce();
        BigInteger epochNumber = cfx.getEpochNumber().sendAndGet();
        RawTransaction tx = RawTransaction.create(nonce, 
                gas, 
                new Address(to), 
                value, 
                storageLimit, 
                epochNumber, 
                data);
        
        String hash = account.send(tx).getTxHash();
        System.out.println("hash===============================" + hash);
        
        return hash;
        } catch(Exception e) {
                e.printStackTrace();
                }
        
        
        return null;
    }

}
