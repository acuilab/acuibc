package com.acuilab.bc.cfx.guguo;

import com.acuilab.bc.cfx.CFXBlockChain;
import com.acuilab.bc.main.cfx.CFXExtend;
import com.acuilab.bc.main.cfx.dapp.guguo.IStakingXIANGContract;
import conflux.web3j.Account;
import conflux.web3j.Cfx;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.contract.abi.DecodeUtil;
import conflux.web3j.contract.abi.TupleDecoder;
import conflux.web3j.types.Address;
import java.math.BigInteger;
import org.javatuples.Sextet;
import org.openide.util.Lookup;
import org.web3j.utils.Numeric;

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
        return account.call(new Account.Option().withGasPrice(this.gasPrice()).withGasLimit(this.gasLimit()),new Address(STAKING_XIANG_CONTRACT), "withdrawPool", new org.web3j.abi.datatypes.Uint(pId));
    }
    
    @Override
    public String withdrawPoolAll(String privateKey) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        Account account = Account.create(cfx, privateKey);
        return account.call(new Account.Option().withGasPrice(this.gasPrice()).withGasLimit(this.gasLimit()),new Address(STAKING_XIANG_CONTRACT), "withdrawPoolAll");
    }

    @Override
    public String depositERC20(String privateKey, BigInteger amount, BigInteger pId) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        Account account = Account.create(cfx, privateKey);
        return account.call(new Account.Option().withGasPrice(this.gasPrice()).withGasLimit(this.gasLimit()),new Address(STAKING_XIANG_CONTRACT), "depositERC20", new org.web3j.abi.datatypes.Uint(pId), new org.web3j.abi.datatypes.Uint(amount));
        //return account.call(new Address(STAKING_XIANG_CONTRACT), "depositERC20", new org.web3j.abi.datatypes.Uint(pId), new org.web3j.abi.datatypes.Uint(amount));
    }

    @Override
    public String withdrawERC20(String privateKey, BigInteger amount, BigInteger pId) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        Account account = Account.create(cfx, privateKey);
        return account.call(new Account.Option().withGasPrice(this.gasPrice()).withGasLimit(this.gasLimit()),new Address(STAKING_XIANG_CONTRACT), "withdrawERC20", new org.web3j.abi.datatypes.Uint(pId), new org.web3j.abi.datatypes.Uint(amount));
    }

    @Override
    public BigInteger getUserCount() {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        ContractCall contract = new ContractCall(cfx, new Address(STAKING_XIANG_CONTRACT));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        // TODO: conflux.web3j.RpcException: RPC error: code = -32015, message = Transaction reverted, data = "0x"
        String value = contract.call("getUserCount").sendAndGet();
        return DecodeUtil.decode(value, org.web3j.abi.datatypes.generated.Uint256.class);
    }

    @Override
    public Sextet<String[], BigInteger[], BigInteger[], BigInteger[], BigInteger[], BigInteger[]> peekUserData(BigInteger start, BigInteger takes) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        ContractCall contract = new ContractCall(cfx, new Address(STAKING_XIANG_CONTRACT));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("peekUserData", new org.web3j.abi.datatypes.generated.Uint256(start), new org.web3j.abi.datatypes.generated.Uint256(takes)).sendAndGet();
        
        return decodePeekUserData(value);
    }

    /**
     * 返回值是六个动态数组
     * @param encoded
     * @return 
     */
    private Sextet<String[], BigInteger[], BigInteger[], BigInteger[], BigInteger[], BigInteger[]> decodePeekUserData(String encoded) {
        encoded = Numeric.cleanHexPrefix(encoded);
        
        TupleDecoder decoder = new TupleDecoder(encoded);
        decoder.nextUint256();  // 跳过offset0
        decoder.nextUint256();  // 跳过offset1
        decoder.nextUint256();  // 跳过offset2
        decoder.nextUint256();  // 跳过offset3
        decoder.nextUint256();  // 跳过offset4
        decoder.nextUint256();  // 跳过offset5
        
        // 第一个动态数组
        CFXExtend cfxExtend = Lookup.getDefault().lookup(CFXExtend.class);
        int length = decoder.nextUint256().intValueExact();
        String[] value0 = new String[length];
        for(int i=0; i<length; i++) {
            value0[i] = cfxExtend.convertAddress(decoder.nextAddress());
        }
        // 第二个动态数组
        length = decoder.nextUint256().intValueExact();
        BigInteger[] value1 = new BigInteger[length];
        for(int i=0; i<length; i++) {
            value1[i] = decoder.nextUint256();
        }
        // 第三个动态数组
        length = decoder.nextUint256().intValueExact();
        BigInteger[] value2 = new BigInteger[length];
        for(int i=0; i<length; i++) {
            value2[i] = decoder.nextUint256();
        }
        // 第四个动态数组
        length = decoder.nextUint256().intValueExact();
        BigInteger[] value3 = new BigInteger[length];
        for(int i=0; i<length; i++) {
            value3[i] = decoder.nextUint256();
        }
        // 第五个动态数组
        length = decoder.nextUint256().intValueExact();
        BigInteger[] value4 = new BigInteger[length];
        for(int i=0; i<length; i++) {
            value4[i] = decoder.nextUint256();
        }
        // 第六个动态数组
        length = decoder.nextUint256().intValueExact();
        BigInteger[] value5 = new BigInteger[length];
        for(int i=0; i<length; i++) {
            value5[i] = decoder.nextUint256();
        }
        
        return new Sextet<>(value0, value1, value2, value3, value4, value5);
    }
    
    public int gasLimit() {
	return 14999999;
    }
    
    public BigInteger gasPrice() {
	return BigInteger.valueOf(1100000000l);
    }
}
