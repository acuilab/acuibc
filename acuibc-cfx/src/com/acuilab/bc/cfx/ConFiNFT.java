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
public class ConFiNFT implements INFT {
    
    public static final String CONTRACT_ADDRESS = "0x859ed8d97707be2c62679fc3505cdf5a77b2c5af";

    @Override
    public void init() {
    }

    @Override
    public String getName() {
	return "ConFi NFT";
    }

    @Override
    public String getSymbol() {
	return "ConFi";
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
        return ImageUtilities.loadImageIcon("/resource/confi" + size + ".png", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/confi" + size + ".png", true);
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
	
	// 8016{"body":"000","eye":"013","nose":"001","auxiliary":"001","background":"000","clothes":"006","props":"000","title":"002_000_000_013_001_000_001_000","url":"002_000_000_013_001_000_001_000.png"}
	MetaData md = new MetaData();
	md.setName("ConFi NFT");
	md.setPlatform("ConFi City");
	
	String id = tokenId.toString();
	md.setId(id);
	
	ObjectMapper mapper = new ObjectMapper();
	Map<String, String> map = mapper.readValue(StringUtils.substringAfter(json, id), Map.class);
	md.setImageUrl("http://cdn.tspace.online/image/finish/" + map.get("url"));
	String title = map.get("title");
	switch(StringUtils.substringBefore(title, "_")) {
	    case "001":
		md.setDesc("明星烤仔");
		break;
	    case "002":
		md.setDesc("烤仔与烤喵");
		break;
	    case "003":
		md.setDesc("天使烤仔");
		break;
	    case "004":
		md.setDesc("恶魔烤仔");
		break;
	    case "005":
		md.setDesc("矿工烤仔");
		break;
	    case "006":
		md.setDesc("金鼠烤仔");
		break;
	    case "007":
		md.setDesc("博士烤仔");
		break;
	    case "008":
		md.setDesc("嘻哈烤仔");
		break;
	    default:
		md.setDesc("");
	}
	
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
