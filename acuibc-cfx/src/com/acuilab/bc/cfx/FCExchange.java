package com.acuilab.bc.cfx;

import static com.acuilab.bc.cfx.CFXCoin.STAKING_CONTRACT_ADDRESS;
import com.acuilab.bc.main.cfx.IFCExchange;
import conflux.web3j.Account;
import conflux.web3j.Cfx;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.contract.abi.DecodeUtil;
import conflux.web3j.contract.abi.TupleDecoder;
import conflux.web3j.types.Address;
import java.math.BigInteger;
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
    public UserInfo userInfos(String address) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        ContractCall contract = new ContractCall(cfx, new Address(CONTRACT_ADDRESS));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("userInfos", new Address(address).getABIAddress()).sendAndGet();
        TupleDecoder decoder = new TupleDecoder(value);
        
        return new UserInfo(decoder.nextUint256(), 
                decoder.nextUint256(), 
                decoder.nextUint256(), 
                decoder.nextBool(), 
                decoder.nextUint256(), 
                decoder.nextUint256());
    }

    @Override
    public BigInteger accCfxPerFc() {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        ContractCall contract = new ContractCall(cfx, new Address(CONTRACT_ADDRESS));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("accCfxPerFc").sendAndGet();
        return DecodeUtil.decode(value, org.web3j.abi.datatypes.Uint.class);
    }

    @Override
    public BigInteger fcSupply() {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        ContractCall contract = new ContractCall(cfx, new Address(CONTRACT_ADDRESS));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("fcSupply").sendAndGet();
        return DecodeUtil.decode(value, org.web3j.abi.datatypes.Uint.class);
    }

    @Override
    public BigInteger lastStakingAmount() {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        ContractCall contract = new ContractCall(cfx, new Address(CONTRACT_ADDRESS));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("lastStakingAmount").sendAndGet();
        return DecodeUtil.decode(value, org.web3j.abi.datatypes.Uint.class);
    }

    @Override
    public String withdrawPendingProfit(String privateKey) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        Account account = Account.create(cfx, privateKey);
        return account.call(new Account.Option().withGasPrice(BigInteger.valueOf(2000000l)).withGasLimit(BigInteger.valueOf(1500000l)), new Address(CONTRACT_ADDRESS), "withdraw", new org.web3j.abi.datatypes.Uint(BigInteger.ZERO));
    }

}
