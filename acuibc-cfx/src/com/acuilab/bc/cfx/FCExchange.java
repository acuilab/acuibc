package com.acuilab.bc.cfx;

import com.acuilab.bc.main.cfx.IFCExchange;
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
    public BigInteger accCfxPerFc(String address) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        ContractCall contract = new ContractCall(cfx, new Address(CONTRACT_ADDRESS));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("accCfxPerFc", new Address(address).getABIAddress()).sendAndGet();
        return DecodeUtil.decode(value, org.web3j.abi.datatypes.Uint.class);
    }

    @Override
    public BigInteger fcSupply(String address) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        ContractCall contract = new ContractCall(cfx, new Address(CONTRACT_ADDRESS));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("fcSupply", new Address(address).getABIAddress()).sendAndGet();
        return DecodeUtil.decode(value, org.web3j.abi.datatypes.Uint.class);
    }

    @Override
    public BigInteger lastStakingAmount(String address) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        ContractCall contract = new ContractCall(cfx, new Address(CONTRACT_ADDRESS));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("lastStakingAmount", new Address(address).getABIAddress()).sendAndGet();
        return DecodeUtil.decode(value, org.web3j.abi.datatypes.Uint.class);
    }

}
