package com.acuilab.bc.cfx.guguo;

import com.acuilab.bc.cfx.CFXBlockChain;
import com.acuilab.bc.main.cfx.dapp.guguo.IStakingXIANGContract;
import conflux.web3j.Account;
import conflux.web3j.Cfx;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.contract.abi.DecodeUtil;
import conflux.web3j.contract.abi.TupleDecoder;
import conflux.web3j.types.Address;
import java.math.BigInteger;
import org.openide.util.Lookup;

/**
 *
 * @author acuilab.com
 */
public class StakingXIANGContract implements IStakingXIANGContract {
    
    public static final String STAKING_XIANG_CONTRACT = "cfx:acbw55y0afshy65xfg7h3bz35516vweb8u48mrjk1s";
    
    @Override
    public BigInteger xiangBalance(String address) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        ContractCall contract = new ContractCall(cfx, new Address(STAKING_XIANG_CONTRACT));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("balanceOf", new Address(address).getABIAddress()).sendAndGet();
        return DecodeUtil.decode(value, org.web3j.abi.datatypes.Uint.class);
    }

    @Override
    public BigInteger poolPledged(BigInteger pId) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        ContractCall contract = new ContractCall(cfx, new Address(STAKING_XIANG_CONTRACT));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("poolInfo", new org.web3j.abi.datatypes.Uint(pId)).sendAndGet();
        TupleDecoder decoder = new TupleDecoder(value);
        decoder.nextAddress();
        return decoder.nextUint256();
    }

    @Override
    public BigInteger pledgedAmount(String address, BigInteger pId) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        ContractCall contract = new ContractCall(cfx, new Address(STAKING_XIANG_CONTRACT));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("userInfo", new org.web3j.abi.datatypes.Uint(pId), new Address(address).getABIAddress()).sendAndGet();
        TupleDecoder decoder = new TupleDecoder(value);
        return decoder.nextUint256();
    }

    @Override
    public BigInteger pendingToken(String address, BigInteger pId) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        ContractCall contract = new ContractCall(cfx, new Address(STAKING_XIANG_CONTRACT));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("pendingToken", new org.web3j.abi.datatypes.Uint(pId), new Address(address).getABIAddress()).sendAndGet();
        return DecodeUtil.decode(value, org.web3j.abi.datatypes.Uint.class);
    }
    
    @Override
    public String withdrawPool(String privateKey, BigInteger pId) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        Account account = Account.create(cfx, privateKey);
        return account.call(new Address(STAKING_XIANG_CONTRACT), "withdrawPool", new org.web3j.abi.datatypes.Uint(pId));
    }
    
    @Override
    public String withdrawPoolAll(String privateKey) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        Account account = Account.create(cfx, privateKey);
        return account.call(new Address(STAKING_XIANG_CONTRACT), "withdrawPoolAll");
    }

    @Override
    public String depositERC20(String privateKey, BigInteger amount, BigInteger pId) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        Account account = Account.create(cfx, privateKey);
        return account.call(new Address(STAKING_XIANG_CONTRACT), "depositERC20", new org.web3j.abi.datatypes.Uint(pId), new org.web3j.abi.datatypes.Uint(amount));
    }

    @Override
    public String withdrawERC20(String privateKey, BigInteger amount, BigInteger pId) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        Account account = Account.create(cfx, privateKey);
        return account.call(new Address(STAKING_XIANG_CONTRACT), "withdrawERC20", new org.web3j.abi.datatypes.Uint(pId), new org.web3j.abi.datatypes.Uint(amount));
    }

}
