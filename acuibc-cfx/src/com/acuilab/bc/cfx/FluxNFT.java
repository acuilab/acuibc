package com.acuilab.bc.cfx;

import com.acuilab.bc.main.nft.MetaData;
import com.acuilab.bc.main.util.Constants;
import com.google.common.collect.Lists;
import conflux.web3j.Cfx;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.contract.abi.DecodeUtil;
import conflux.web3j.types.Address;
import java.awt.Image;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Type;

/**
 *
 * @author 78429
 */
public class FluxNFT extends AbstractNFT {
    
    public static final String CONTRACT_ADDRESS = "cfx:acfs5g80h7khw4mxg0pa78e92t8ywp6h8pr69bx7rb";
    public static final String WEBSITE = "https://flux.01.finance/";

    @Override
    public void init() {
    }

    @Override
    public String getName() {
        return "Flux NFT";
    }

    @Override
    public String getSymbol() {
	return "Flux";
    }

    @Override
    public String getContractAddress() {
	return CONTRACT_ADDRESS;
    }

    @Override
    public String getWebsite() {
        return WEBSITE;
    }

    @Override
    public String getBlockChainSymbol() {
        return Constants.CFX_BLOCKCHAIN_SYMBAL;
    }

    @Override
    public Icon getIcon(int size) {
        return ImageUtilities.loadImageIcon("/resource/flux" + size + ".png", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/flux" + size + ".png", true);
    }

    @Override
    public MetaData getMetaData(BigInteger tokenId) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
	ContractCall contract = new ContractCall(cfx, new Address(CONTRACT_ADDRESS));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("uri", new org.web3j.abi.datatypes.Uint(tokenId)).sendAndGet();
        String json = DecodeUtil.decode(value, org.web3j.abi.datatypes.Utf8String.class);
        MetaData md = new MetaData();

        String id = tokenId.toString();
	md.setId(id);
        md.setImageUrl(json);
        // TODO: 设置名称、描述和平台
        
        md.setPlatform("Flux");
        switch (tokenId.intValue()){
            case 1:
                md.setName("大户NFT");
                md.setDesc("大户NFT（内含10枚代币）");
                break;
            case 2:
                md.setName("微光NFT");
                md.setDesc("微光NFT（内含一枚代币）");
                break;
            case 3:
                md.setName("守护者NFT");
                md.setDesc("守护者NFT（特殊贡献）");
                break;
            case 4:
                md.setName("Salt NFT");
                md.setDesc("Salt NFT（专为Salt）");
                break;
            case 5:
                md.setName("传播者nft");
                md.setDesc("传播者nft（内含2枚代币）");
                break;
            case 6:
                md.setName("WSB NFT");
                md.setDesc("WSB NFT（外国社区）");
                break;
            default:
                md.setName("未知nft");
                md.setDesc("未知nft");              
        }
	
	return md;
    }

    @Override
    public BigInteger[] tokensOf(String address) {
        return new BigInteger[] {
            BigInteger.valueOf(1l), 
            BigInteger.valueOf(2l), 
            BigInteger.valueOf(3l), 
            BigInteger.valueOf(4l), 
            BigInteger.valueOf(5l), 
            BigInteger.valueOf(6l)
        };
        // conflux.web3j.RpcException: RPC error: code = -32015, message = Transaction reverted, data = "0x"
//        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
//        Cfx cfx = bc.getCfx();
//	ContractCall contract = new ContractCall(cfx, new Address(getContractAddress()));
//        // passing method name and parameter to `contract.call`
//        // note: parameters should use web3j.abi.datatypes type
//        org.web3j.abi.datatypes.Address abiAddress = new Address(address).getABIAddress();
//        String value = contract.call("balanceOfBatch", 
//                new Type<?>[] {new DynamicArray(org.web3j.abi.datatypes.Address.class, new org.web3j.abi.datatypes.Address[] {
//                    abiAddress,
//                    abiAddress,
//                    abiAddress,
//                    abiAddress,
//                    abiAddress,
//                    abiAddress
//                }), new DynamicArray(org.web3j.abi.datatypes.Uint.class, new org.web3j.abi.datatypes.Uint[] {
//                    new org.web3j.abi.datatypes.Uint(BigInteger.valueOf(1l)), 
//                    new org.web3j.abi.datatypes.Uint(BigInteger.valueOf(2l)), 
//                    new org.web3j.abi.datatypes.Uint(BigInteger.valueOf(3l)), 
//                    new org.web3j.abi.datatypes.Uint(BigInteger.valueOf(4l)), 
//                    new org.web3j.abi.datatypes.Uint(BigInteger.valueOf(5l)), 
//                    new org.web3j.abi.datatypes.Uint(BigInteger.valueOf(6l))})}
//                ).sendAndGet();
//        
//        List<org.web3j.abi.datatypes.Uint> valueDecode = DecodeUtil.decode(value, new TypeReference<DynamicArray<org.web3j.abi.datatypes.Uint>>() {});
//	// 取非0值转成BigInteger数组
//        List<BigInteger> ret = Lists.newArrayList();
//	for(int i=0; i<valueDecode.size(); i++) {
//            if(valueDecode.get(i).getValue().compareTo(BigInteger.ZERO) > 0) {
//                ret.add(BigInteger.valueOf(i+1));
//            }
//	}
//	return ret.toArray(new BigInteger[] {});
    }

//    @Override
//    public String safeTransferFrom(String privateKey, String from, String to, BigInteger tokenId, BigInteger gas) throws Exception {
//        return super.safeTransferFrom(privateKey, from, to, tokenId, gas); //To change body of generated methods, choose Tools | Templates.
//    }

    @Override
    public BigInteger balanceOf(String address, BigInteger tokenId) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
	ContractCall contract = new ContractCall(cfx, new Address(CONTRACT_ADDRESS));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("balanceOf", new Address(address).getABIAddress(), new org.web3j.abi.datatypes.Uint(tokenId)).sendAndGet();
        
        return DecodeUtil.decode(value, org.web3j.abi.datatypes.Uint.class);
    }
    
}
