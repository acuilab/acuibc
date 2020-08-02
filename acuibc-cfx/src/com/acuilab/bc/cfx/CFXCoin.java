package com.acuilab.bc.cfx;

import com.acuilab.bc.main.coin.Coin;
import conflux.web3j.Account;
import conflux.web3j.Cfx;
import conflux.web3j.Request;
import conflux.web3j.response.BigIntResponse;
import conflux.web3j.types.RawTransaction;
import java.math.BigInteger;
import org.openide.util.Lookup;

/**
 *
 * @author admin
 */
public class CFXCoin implements Coin {

    @Override
    public String getName() {
        return "CFX";
    }

    @Override
    public String getSymbol() {
        return "CFX";
    }

    @Override
    public Type getType() {
        return Type.BASE;
    }

    @Override
    public BigInteger balanceOf(String address) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        Request<BigInteger, BigIntResponse> req = cfx.getBalance(address);
        
        return req.sendAndGet();
    }


    @Override
    public void transferRecord() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * 转账
     * @param to        接收地址
     * @param value     转账数量
     * @param gas       矿工费
     */
    @Override
    public String transfer(String privateKey, String to, BigInteger value, BigInteger gas) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        Account account = Account.create(cfx, privateKey);
        BigInteger currentEpoch = cfx.getEpochNumber().sendAndGet();
        
        return account.mustSend(RawTransaction.create(account.getNonce(), gas, to, value, BigInteger.ZERO, currentEpoch, null));
    }
    
}
