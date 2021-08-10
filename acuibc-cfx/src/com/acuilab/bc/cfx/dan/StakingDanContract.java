package com.acuilab.bc.cfx.dan;

import com.acuilab.bc.cfx.CFXBlockChain;
import com.acuilab.bc.main.cfx.CFXExtend;
import com.acuilab.bc.main.cfx.dapp.dan.IStakingDanContract;
import conflux.web3j.Cfx;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.contract.abi.TupleDecoder;
import conflux.web3j.types.Address;
import java.math.BigInteger;
import org.javatuples.Octet;
import org.javatuples.Sextet;
import org.openide.util.Lookup;
import org.web3j.utils.Numeric;

/**
 *
 * @author acuilab.com
 */
public class StakingDanContract implements IStakingDanContract {
    
    // 质押合约
    public static final String STAKING_DAN_CONTRACT = "cfx:acgxtee8wbg3vvmd53gg4abf70n9ssfykjckfmat3d";
    // 附加合约,帮忙前端做batch用
    public static final String STAKING_AUXILIARY_CONTRACT = "cfx:acaa69t5t1vcnp29u5r8d60fa51d1b6bz2chn8vhpn";

    @Override
    public Octet<BigInteger, BigInteger, BigInteger, Boolean, String[], BigInteger[], BigInteger[], BigInteger[]> stakingDetail(String account) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        ContractCall contract = new ContractCall(cfx, new Address(STAKING_AUXILIARY_CONTRACT));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("stakingDetail", new Address(STAKING_DAN_CONTRACT).getABIAddress(), new Address(account).getABIAddress()).sendAndGet();
        
        return decodeStakingDetail(value);
    }
    
    /**
     * 返回值是六个动态数组
     * @param encoded
     * @return 
     */
    private Octet<BigInteger, BigInteger, BigInteger, Boolean, String[], BigInteger[], BigInteger[], BigInteger[]> decodeStakingDetail(String encoded) {
        encoded = Numeric.cleanHexPrefix(encoded);
        
        TupleDecoder decoder = new TupleDecoder(encoded);
        BigInteger stakingPoints = decoder.nextUint256();
        BigInteger totalStaked = decoder.nextUint256();
        BigInteger estimateBlockPerDan = decoder.nextUint256();
        Boolean isInCurrentPeriod = decoder.nextBool();
        
        decoder.nextUint256();  // 跳过
        decoder.nextUint256();  // 跳过
        decoder.nextUint256();  // 跳过
        decoder.nextUint256();  // 跳过
        
        // 第一个动态数组tokenAddr
        CFXExtend cfxExtend = Lookup.getDefault().lookup(CFXExtend.class);
        int length = decoder.nextUint256().intValueExact();
        String[] value0 = new String[length];
        for(int i=0; i<length; i++) {
            value0[i] = cfxExtend.convertAddress(decoder.nextAddress());
        }
        
        // 第二个动态数组stakings
        length = decoder.nextUint256().intValueExact();
        BigInteger[] value1 = new BigInteger[length];
        for(int i=0; i<length; i++) {
            value1[i] = decoder.nextUint256();
        }
        
        // 第三个动态数组balances
        length = decoder.nextUint256().intValueExact();
        BigInteger[] value2 = new BigInteger[length];
        for(int i=0; i<length; i++) {
            value2[i] = decoder.nextUint256();
        }
        
        // 第四个动态数组allowances
        length = decoder.nextUint256().intValueExact();
        BigInteger[] value3 = new BigInteger[length];
        for(int i=0; i<length; i++) {
            value3[i] = decoder.nextUint256();
        }
        
        return new Octet<>(stakingPoints, totalStaked, estimateBlockPerDan, isInCurrentPeriod, value0, value1, value2, value3);
    }
}
