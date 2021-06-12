package com.acuilab.bc.cfx.guguo;

import com.acuilab.bc.cfx.CFXBlockChain;
import com.acuilab.bc.cfx.YAOCoin;
import com.acuilab.bc.cfx.YAO_CFXPair;
import conflux.web3j.Cfx;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.contract.abi.DecodeUtil;
import conflux.web3j.contract.abi.TupleDecoder;
import conflux.web3j.types.Address;
import java.math.BigInteger;
import org.javatuples.Pair;
import org.openide.util.Lookup;
import org.web3j.utils.Numeric;
import com.acuilab.bc.main.cfx.dapp.guguo.IStakingYAOContract;
import conflux.web3j.Account;

/**
 *
 * @author acuilab.com
 */
public class StakingYAOContract implements IStakingYAOContract {
    
    public static final String STAKING_YAO_CONTRACT = "cfx:acgxme0vx2uychduh9thtzgx4yy0mkrgde0uxcza7d";
    public static final String STAKING_XIANG_CONTRACT = "cfx:acbw55y0afshy65xfg7h3bz35516vweb8u48mrjk1s";
    public static final String PICK_CARD_CONTRACT = "cfx:acbp6r5kpgvz3pcxax557r2xrnk4rv9f02tpkng9ne";
    
    @Override
    public BigInteger yaoBalance(String address) {
        YAOCoin yaoCoin = Lookup.getDefault().lookup(YAOCoin.class);
        return yaoCoin.balanceOf(address);
    }
    
    @Override
    public BigInteger yaoCfxBalance(String address) {
        YAO_CFXPair yaoCfxPair = Lookup.getDefault().lookup(YAO_CFXPair.class);
        return yaoCfxPair.balanceOf(address);
    }

    @Override
    public Pair<BigInteger[], BigInteger[]> pledgedERC1155(String address, int pid) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        ContractCall contract = new ContractCall(cfx, new Address(STAKING_YAO_CONTRACT));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("pledgedERC1155", new org.web3j.abi.datatypes.Uint(BigInteger.valueOf(pid)), new Address(address).getABIAddress()).sendAndGet();
        
        return decodepLedgedERC1155(value);
    }

    /**
     * 返回值是两个动态数组，第一个是tokenId列表，第二个是数量列表
     * @param encoded
     * @return 
     */
    private Pair<BigInteger[], BigInteger[]> decodepLedgedERC1155(String encoded) {
        encoded = Numeric.cleanHexPrefix(encoded);
        
        TupleDecoder decoder = new TupleDecoder(encoded);
        decoder.nextUint256();  // 跳过offset0
        decoder.nextUint256();  // 跳过offset1
        
        // 第一个动态数组
        int length = decoder.nextUint256().intValueExact();
        BigInteger[] value0 = new BigInteger[length];
        for(int i=0; i<length; i++) {
            value0[i] = decoder.nextUint256();
            System.out.println("value0[" + i + "]" + value0[i].toString());
        }
        // 第二个动态数组
        length = decoder.nextUint256().intValueExact();
        BigInteger[] value1 = new BigInteger[length];
        for(int i=0; i<length; i++) {
            value1[i] = decoder.nextUint256();
            System.out.println("value1[" + i + "]" + value1[i].toString());
        }
        
        return new Pair<>(value0, value1);
    }

    @Override
    public BigInteger totalReleased() {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        ContractCall contract = new ContractCall(cfx, new Address(STAKING_YAO_CONTRACT));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        // TODO: conflux.web3j.RpcException: RPC error: code = -32015, message = Transaction reverted, data = "0x"
        String value = contract.call("totalReleased").sendAndGet();
        return DecodeUtil.decode(value, org.web3j.abi.datatypes.Uint.class);
    }

    @Override
    public BigInteger pendingToken(String address, int pId) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        ContractCall contract = new ContractCall(cfx, new Address(STAKING_YAO_CONTRACT));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("pendingToken", new org.web3j.abi.datatypes.Uint(BigInteger.valueOf(pId)), new Address(address).getABIAddress()).sendAndGet();
        
        return DecodeUtil.decode(value, org.web3j.abi.datatypes.Uint.class);
    }

    @Override
    public String withdrawPoolAll(String privateKey) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        Account account = Account.create(cfx, privateKey);
        return account.call(new Address(STAKING_YAO_CONTRACT), "withdrawPoolAll");
    }
}
