package com.acuilab.bc.cfx;

import com.acuilab.bc.main.nft.INFT;
import com.acuilab.bc.main.nft.MetaData;
import com.fasterxml.jackson.databind.ObjectMapper;
import conflux.web3j.Account;
import conflux.web3j.Account.Option;
import conflux.web3j.Cfx;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.contract.abi.DecodeUtil;
import java.awt.Image;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import org.apache.commons.lang3.StringUtils;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Type;

/**
 *
 * @author admin
 */
public class ConfluxGuardianNFT implements INFT {
    
    public static final String CONTRACT_ADDRESS = "0x8fd17cadc3931d94afff8543005637f3fffeb769";

    @Override
    public void init() {
    }

    @Override
    public String getName() {
	return "Conflux Guardian NFT";
    }

    @Override
    public String getSymbol() {
	return "CG";
    }

    @Override
    public String getBlockChainSymbol() {
        return CFXBlockChain.SYMBOL;
    }
    
    @Override
    public String getContractAddress() {
	return CONTRACT_ADDRESS;
    } 

    @Override
    public Icon getIcon(int size) {
        return ImageUtilities.loadImageIcon("/resource/ConfluxGuardian" + size + ".png", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/ConfluxGuardian" + size + ".png", true);
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
    public MetaData getMetaData(BigInteger tokenId) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
	ContractCall contract = new ContractCall(cfx, CONTRACT_ADDRESS);
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("uri", new org.web3j.abi.datatypes.Uint(tokenId)).sendAndGet();
        String json = DecodeUtil.decode(value, org.web3j.abi.datatypes.Utf8String.class);
	
        MetaData md = new MetaData();
        System.out.println(value.toString());
        System.out.println(json.toString());
        
	String id = tokenId.toString();
	md.setId(id);
	md.setImageUrl("https://cdn.img.imakejoy.com/guardian/nft.png");
        md.setDesc("累计质押满1000FC的用户将获得一个NFT作为纪念徽章。");
        md.setName("守护者徽章");
	md.setPlatform("Conflux");
	
	return md;
    }

    @Override
    public String safeTransferFrom(String privateKey, String from, String to, BigInteger tokenId, String data) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
	
	byte[] bytes = StringUtils.getBytes(data, Charset.forName("UTF-8"));
	System.out.println("bytes.length=======" + bytes.length);
        
        Account account = Account.create(cfx, privateKey);
	return account.call(new Option(), CONTRACT_ADDRESS, "safeTransferFrom", 
            new org.web3j.abi.datatypes.Address(from), 
	    new org.web3j.abi.datatypes.Address(to), 
	    new org.web3j.abi.datatypes.Uint(tokenId), 
            new org.web3j.abi.datatypes.Uint(BigInteger.ONE), 
	    new org.web3j.abi.datatypes.DynamicBytes(data.getBytes()));
    }
}
