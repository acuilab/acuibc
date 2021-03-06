package com.acuilab.bc.cfx;

import com.acuilab.bc.main.nft.MetaData;
import com.acuilab.bc.main.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import conflux.web3j.Cfx;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.contract.abi.DecodeUtil;
import conflux.web3j.types.Address;
import java.awt.Image;
import java.math.BigInteger;
import java.util.Map;
import javax.swing.Icon;
import org.apache.commons.lang3.StringUtils;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

/**
 *
 * @author admin
 */
public class TreaNFT extends AbstractNFT {
    
    public static final String CONTRACT_ADDRESS = "cfx:accfeg3rcm430khhbz09r4t38aswm5u9dezucjxjcf";
    public static final String WEBSITE = "http://trea.finance/conflux/";

    @Override
    public void init() {
    }

    @Override
    public String getName() {
	return "熬鹰";
    }

    @Override
    public String getSymbol() {
	return "Trea NFT";
    }

    @Override
    public String getBlockChainSymbol() {
        return Constants.CFX_BLOCKCHAIN_SYMBAL;
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
    public Icon getIcon(int size) {
        return ImageUtilities.loadImageIcon("/resource/trea" + size + ".png", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/trea" + size + ".png", true);
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
	
        System.out.println(json);
	// 8016{"body":"000","eye":"013","nose":"001","auxiliary":"001","background":"000","clothes":"006","props":"000","title":"002_000_000_013_001_000_001_000","url":"002_000_000_013_001_000_001_000.png"}
	MetaData md = new MetaData();
	
	
	
	String id = tokenId.toString();
	md.setId(id);
        md.setNumber(id);
        
        
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(StringUtils.substringAfter(json, id), Map.class);
        //Map<String, String> map = mapper.readValue(new URL(json), Map.class);

        String imageUrl = map.get("image");
        //String imageUrlSlim = StringUtils.replace(imageUrl, "\\", "");
        //System.out.println(imageUrl);
	md.setImageUrl(imageUrl);
        md.setDesc(map.get("description"));
        md.setName(map.get("name"));
	md.setPlatform("Trea");
        
        
	switch(md.getName()) {
	    case "巨鼎":
		md.setDesc("高额参与Trea创世轮游戏66666硬顶的限量纪念NFT");
		break;
	    case "万贯":
		md.setDesc("赞助Trea创世轮游戏66666硬顶的限量纪念NFT");
		break;
	    case "飞天":
		md.setDesc("参与Trea创世轮游戏66666硬顶的限量纪念NFT");
		break;
	    
	    default:
		md.setDesc("无");
	}
	
	
	return md;
    }
}
