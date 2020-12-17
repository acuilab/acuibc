package com.acuilab.bc.cfx;

import com.acuilab.bc.main.nft.INFT;
import com.acuilab.bc.main.nft.MetaData;
import com.fasterxml.jackson.databind.ObjectMapper;
import conflux.web3j.Account;
import conflux.web3j.Account.Option;
import conflux.web3j.Cfx;
import conflux.web3j.CfxUnit;
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
public class ConDragonNFT implements INFT {
    
    public static final String CONTRACT_ADDRESS = "0x83928828f200b79b78404dce3058ba0c8c4076c3";

    @Override
    public void init() {
    }

    @Override
    public String getName() {
	return "ConDragon NFT";
    }

    @Override
    public String getSymbol() {
	return "ConDragon";
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
        return ImageUtilities.loadImageIcon("/resource/condragon" + size + ".png", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/condragon" + size + ".png", true);
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
	System.out.println(value.toString());
        System.out.println(json.toString());
        
        MetaData md = new MetaData();
        
        
//        {
//	"token_id": "3985",
//	"image": "https:\/\/cdn.img.imakejoy.com\/condragon\/2-1.png",
//	"description": "\u4f53\u578b\u5177\u6709\u7edd\u5bf9\u7684\u53c8\u662f\uff0c\u4f46\u662f\u4f7f\u5176\u884c\u52a8\u80fd\u529b\u4e25\u91cd\u53d7\u963b\uff0c\u6e29\u987a\u548c\u7766\u5f88\u5bb9\u6613\u9a6f\u670d\uff0c\u662f\u516c\u8ba4\u6700\u53ef\u9760\u7684\u4f19\u4f34\u3002",
//	"description_en": "It has an absolute advantage in size. However, big size also makes it hard to move quickly. It can be easily tamed and is widely recognized as the most reliable partner.",
//	"name": "\u5251\u9f99",
//	"name_en": "Stegosaurus"
//        }

        String id = tokenId.toString();
	md.setId(id);
	
	ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(new URL(json), Map.class);

        String imageUrl = map.get("image");
        String imageUrlSlim = StringUtils.replace(imageUrl, "\\", "");
        System.out.println(imageUrlSlim);
	md.setImageUrl(imageUrlSlim);
        md.setDesc(map.get("description"));
        md.setName(map.get("name"));
	md.setPlatform("ConDragon");
	
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
