package com.acuilab.bc.cfx;

import com.acuilab.bc.main.nft.INFT;
import conflux.web3j.Cfx;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.contract.abi.DecodeUtil;
import java.awt.Image;
import java.math.BigInteger;
import java.util.List;
import javax.swing.Icon;
import org.openide.util.Lookup;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.DynamicArray;

/**
 *
 * @author admin
 */
public class ConFiNFT implements INFT {
    
    public static final String CONTRACT_ADDRESS = "0x859ed8d97707be2c62679fc3505cdf5a77b2c5af";

    @Override
    public void init() {
    }

    @Override
    public String getName() {
	return "Confi NFT";
    }

    @Override
    public String getSymbol() {
	return "Confi NFT";
    }

    @Override
    public String getBlockChainSymbol() {
        return CFXBlockChain.SYMBOL;
    }

    @Override
    public Icon getIcon(int size) {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Image getIconImage(int size) {
	throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BigInteger[] tokensOf(String address) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
	ContractCall contract = new ContractCall(cfx, CONTRACT_ADDRESS);
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("tokensOf", new org.web3j.abi.datatypes.Address(address)).sendAndGet();
        List<org.web3j.abi.datatypes.Uint> valueDecode = DecodeUtil.decode(value, new TypeReference<DynamicArray<org.web3j.abi.datatypes.Uint>>() {});
	// 转成BigInteger数组
	BigInteger[] ret = new BigInteger[valueDecode.size()];
	for(int i=0; i<valueDecode.size(); i++) {
	    ret[i] = valueDecode.get(i).getValue();
	}
	return ret;
    }
    
    @Override
    public String uri(String address) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
	ContractCall contract = new ContractCall(cfx, CONTRACT_ADDRESS);
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("uri", new org.web3j.abi.datatypes.Address(address)).sendAndGet();
        return DecodeUtil.decode(value, org.web3j.abi.datatypes.Utf8String.class);
    }
}
