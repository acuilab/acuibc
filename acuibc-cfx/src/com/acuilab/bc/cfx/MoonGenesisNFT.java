package com.acuilab.bc.cfx;

import static com.acuilab.bc.cfx.ConFiNFT.CONTRACT_ADDRESS;
import com.acuilab.bc.main.nft.INFT;
import com.acuilab.bc.main.nft.MetaData;
import com.fasterxml.jackson.databind.ObjectMapper;
import conflux.web3j.Account;
import conflux.web3j.Account.Option;
import conflux.web3j.Cfx;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.contract.abi.DecodeUtil;
import java.awt.Image;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import org.apache.commons.lang3.StringUtils;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.DynamicArray;
import java.net.URL;
import java.nio.charset.Charset;
import org.web3j.abi.datatypes.Type;
/**
 *
 * @author admin
 */
public class MoonGenesisNFT implements INFT {
    
    public static final String CONTRACT_ADDRESS = "0x89c9ec494607ae96ae2a36c8c3d0220bc3a51819";

    @Override
    public void init() {
    }

    @Override
    public String getName() {
	return "MoonGenesis NFT";
    }

    @Override
    public String getSymbol() {
	return "MoonGenesis";
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
        return ImageUtilities.loadImageIcon("/resource/cMOON" + size + ".png", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/cMOON" + size + ".png", true);
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
            System.out.println(ret[i]);
	}
	return ret;
    }
    
    @Override
    public MetaData getMetaData(BigInteger tokenId) throws IOException {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
	ContractCall contract = new ContractCall(cfx, CONTRACT_ADDRESS);
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("uri", new org.web3j.abi.datatypes.Uint(tokenId)).sendAndGet();
        String json = DecodeUtil.decode(value, org.web3j.abi.datatypes.Utf8String.class);
        
//	{
//	"token_id": "72",
//	"image": "https:\/\/cdn.img.imakejoy.com\/genesis\/genesis\/0072-0_0_2.png",
//	"description": "\u521b\u4e16NFT\u662f\u4e3a\u4e86\u7eaa\u5ff5MoonSwap\u767b\u6708\u6210\u529f\u800c\u53d1\u884c\u7684NFT\uff0c\u5c06\u9762\u5411\u53c2\u4e0e\u521b\u4e16\u767b\u6708\u7684\u9886\u822a\u5458\u4eec\u6bcf\u4eba\u7a7a\u6295\u4e00\u4e2a\u521b\u4e16NFT\uff0c\u51711024\u4e2a\uff0c\u6c38\u4e0d\u589e\u53d1\u3002",
//	"name": "\u521b\u4e16NFT"
//       }
        
        MetaData md = new MetaData();
	
	String id = tokenId.toString();
	md.setId(id);
	
	ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(new URL(json), Map.class);

        String imageUrl = map.get("image");
        String imageUrl1 = StringUtils.replace(imageUrl, "\\", "");
        String imageUrl2 = StringUtils.replace(imageUrl1, "/genesis", "", 1);

	md.setImageUrl(imageUrl2);
        md.setDesc(map.get("description"));
        md.setName(map.get("name"));
	md.setPlatform("MoonSwap");
	
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
