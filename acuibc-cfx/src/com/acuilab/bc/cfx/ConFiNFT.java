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
public class ConFiNFT extends AbstractNFT {
    
    public static final String CONTRACT_ADDRESS = "cfx:acc370g3s6d56ndcp8t6gyc657rhtp0fz6ytc8j9d2";
    public static final String WEBSITE = "http://nft.tspace.io";

    @Override
    public void init() {
    }

    @Override
    public String getName() {
	return "ConFi";
    }

    @Override
    public String getSymbol() {
	return "ConFi";
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
        return ImageUtilities.loadImageIcon("/resource/confi" + size + ".png", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/confi" + size + ".png", true);
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
	
	// 8016{"body":"000","eye":"013","nose":"001","auxiliary":"001","background":"000","clothes":"006","props":"000","title":"002_000_000_013_001_000_001_000","url":"002_000_000_013_001_000_001_000.png"}
	MetaData md = new MetaData();
	md.setName("ConFi NFT");
	md.setPlatform("ConFi City");
	
	String id = tokenId.toString();
	md.setId(id);
        md.setNumber(id);
	
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
}
