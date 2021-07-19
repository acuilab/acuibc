package com.acuilab.bc.cfx;

import com.acuilab.bc.main.nft.INFT;
import conflux.web3j.Account;
import conflux.web3j.Cfx;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.contract.abi.DecodeUtil;
import conflux.web3j.types.Address;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.openide.util.Lookup;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.DynamicArray;

/**
 *
 * @author admin
 */
public abstract class AbstractNFT implements INFT {
    
    @Override
    public BigInteger[] tokensOf(String address) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
	ContractCall contract = new ContractCall(cfx, new Address(getContractAddress()));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("tokensOf", new Address(address).getABIAddress()).sendAndGet();
        
        List<org.web3j.abi.datatypes.Uint> valueDecode = DecodeUtil.decode(value, new TypeReference<DynamicArray<org.web3j.abi.datatypes.Uint>>() {});
	// 转成BigInteger数组
	BigInteger[] ret = new BigInteger[valueDecode.size()];
	for(int i=0; i<valueDecode.size(); i++) {
	    ret[i] = valueDecode.get(i).getValue();
	}
	return ret;
    }
    
    @Override
    public String safeTransferFrom(String privateKey, String from, String to, BigInteger tokenId, BigInteger value, String data, BigInteger gas) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
	
        Account account = Account.create(cfx, privateKey);
        if(getType() == Type.NFT1155) {
            return account.call(new Account.Option().withGasPrice(gas).withGasLimit(this.gasLimit()), new Address(getContractAddress()), "safeTransferFrom", 
                new Address(from).getABIAddress(),
                new Address(to).getABIAddress(), 
                new org.web3j.abi.datatypes.Uint(tokenId), 
                new org.web3j.abi.datatypes.Uint(value), //1155有这个参数，721没有这个参数。只有这个区别
                new org.web3j.abi.datatypes.DynamicBytes(StringUtils.getBytes(data, Charset.forName("UTF-8"))));
        } else {
            // 721, 忽略value参数
            return account.call(new Account.Option().withGasPrice(gas).withGasLimit(this.gasLimit()), new Address(getContractAddress()), "safeTransferFrom", 
                new Address(from).getABIAddress(),
                new Address(to).getABIAddress(), 
                new org.web3j.abi.datatypes.Uint(tokenId), 
                new org.web3j.abi.datatypes.DynamicBytes(StringUtils.getBytes(data, Charset.forName("UTF-8"))));
        }
    }
    
    @Override
    public int gasMin() {
	// 1drip
        return 1;
    }

    @Override
    public int gasMax() {
        // 100drip
        return 100;
    }

    @Override
    public int gasDefault() {
	// 1 drip
        return 1;
    }
    
    @Override
    public int gasLimit() {
	return 200000;
    }

    @Override
    public String gasDesc(int gas) {
        return gas + "drip";
    }
    
    public Type getType() {
        return Type.NFT1155;
    }
    
    public enum Type {
	NFT1155,    // 1155
	NFT721	    // 721
    }
}
