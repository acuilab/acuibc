package com.acuilab.bc.cfx;

import com.acuilab.bc.main.cfx.IGuGuoContract;
import com.acuilab.bc.main.nft.INFT;
import com.acuilab.bc.main.nft.MetaData;
import conflux.web3j.Cfx;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.contract.abi.DecodeUtil;
import conflux.web3j.types.Address;
import java.math.BigInteger;
import org.openide.util.Lookup;

/**
 *
 * @author acuilab.com
 */
public class GuGuoContract implements IGuGuoContract {
    
    public static final String STAKING_YAO_CONTRACT = "cfx:acgxme0vx2uychduh9thtzgx4yy0mkrgde0uxcza7d";
    public static final String STAKING_XIANG_CONTRACT = "cfx:acbw55y0afshy65xfg7h3bz35516vweb8u48mrjk1s";
    public static final String PICK_CARD_CONTRACT = "cfx:acbp6r5kpgvz3pcxax557r2xrnk4rv9f02tpkng9ne";
    
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
    public BigInteger yaoBalance(String address) {
        YAOCoin yaoCoin = Lookup.getDefault().lookup(YAOCoin.class);
        return yaoCoin.balanceOf(address);
    }

    @Override
    public BigInteger[] pledgedERC1155(String address, int pid) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        ContractCall contract = new ContractCall(cfx, new Address(STAKING_YAO_CONTRACT));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("pledgedERC1155", new org.web3j.abi.datatypes.Uint(BigInteger.valueOf(pid)), new Address(address).getABIAddress()).sendAndGet();
        
        
        System.out.println("value=====================================" + value);
        
//        String encoded = Numeric.cleanHexPrefix(value);
//        TupleDecoder decoder = new TupleDecoder(encoded);
//        
//        BigInteger offset = decoder.nextUint256();
//        BigInteger OFFSET_DYNAMIC_ARRAY = BigInteger.valueOf(Type.MAX_BYTE_LENGTH);
//        System.out.println("offset==========================================" + offset.toString());
//        System.out.println("OFFSET_DYNAMIC_ARRAY==========================================" + OFFSET_DYNAMIC_ARRAY.toString());
        
//        TupleDecoder[] decoders = TupleDecoder.decodeDynamicArray(value);
//        System.out.println("===========================" + decoders[0].nextUint256().toString());
//        System.out.println("===========================" + decoders[0].nextUint256().toString());
//        System.out.println("===========================" + decoders[0].nextUint256().toString());
//        System.out.println("===========================" + decoders[1].nextUint256().toString());
//        System.out.println("===========================" + decoders[1].nextUint256().toString());
//        System.out.println("===========================" + decoders[1].nextUint256().toString());
//        System.out.println("===========================" + decoders[1].nextUint256().toString());
//        
//        Object obj = DecodeUtil.decode(value, new TypeReference.StaticArrayTypeReference<DynamicArray<org.web3j.abi.datatypes.Uint>>(2) {});
//        
//        List<org.web3j.abi.datatypes.Uint> list2 = list.get(0).getValue();
//        
//        for(org.web3j.abi.datatypes.Uint i : list2) {
//            System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx=" + i.getValue().toString());
//        }
        
        
        return null;
    }

    @Override
    public MetaData getMetaData(int pId, BigInteger tokenId) throws Exception {
        switch (pId) {
            case KAOZI_PID:
            {
                // 烤仔
                INFT nft = Lookup.getDefault().lookup(ConFiNFT.class);
                return nft.getMetaData(tokenId);
            }
            case MOON_PID:
            {
                // moon
                INFT nft = Lookup.getDefault().lookup(MoonGenesisNFT.class);
                return nft.getMetaData(tokenId);
            }
            case FLUX_PID:
            {            
                // flux
                INFT nft = Lookup.getDefault().lookup(MoonGenesisNFT.class);
                return nft.getMetaData(tokenId);
            }
        
            case GUGUO_PID:
            {
                // guguo
                
                return null;
            }
            default:
                break;
        }
        
        return null;
    }

    
}
