package com.acuilab.bc.cfx;

import com.acuilab.bc.main.nft.MetaData;
import com.acuilab.bc.main.util.Constants;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import conflux.web3j.Cfx;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.contract.abi.DecodeUtil;
import conflux.web3j.types.Address;
import java.awt.Image;
import java.math.BigInteger;
import java.net.URL;
import java.util.Map;
import javax.swing.Icon;
import org.apache.commons.lang3.StringUtils;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

/**
 *
 * @author admin
 */
public class TinyVersusWrapperNFT extends AbstractNFT {
    
    public static final String CONTRACT_ADDRESS = "cfx:cfx:acadspzdxvkvyb0su5y3v2avw2h5py40ruw548c3aj";
    public static final String WEBSITE = "https://www.tinyversus.com/";

    @Override
    public void init() {
    }

    @Override
    public String getName() {
	return "小小乱斗英雄";
    }

    @Override
    public String getSymbol() {
	return "TinyVersusWrapper";
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
        return ImageUtilities.loadImageIcon("/resource/TinyVersus" + size + ".png", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/TinyVersus" + size + ".png", true);
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
	System.out.println("json=" + json);
        // https://www.tinyversus.com:8177/api/frontend/pandaNFT/609.json
        /*
        {
                "image": "https://d1motvw702gmc2.cloudfront.net/image/275392a9a6df.png",
                "avatar": "https://d1motvw702gmc2.cloudfront.net/image/b730df794905.png",
                "localization": null,
                "properties": null,
                "backgroundImage": "https://d1motvw702gmc2.cloudfront.net/image/fb6ec0c86a88.png",
                "parts": [6, 7, 10, 9, 6, 9],
                "name": "Panda #609",
                "description": "Tinyverse Pandarian.",
                "artist": null,
                "artistIntroduction": null,
                "concept": null,
                "viewId": 0
        }
        */
        String id16 = tokenId.toString(16);
        
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(new URL(json), Map.class);
        
//	ObjectMapper mapper = new ObjectMapper();
//        
//        JsonNode rootNode = mapper.readTree(new URL(StringUtils.replace(json, "{id}", id16)));
//        JsonNode imageNode = rootNode.get("image");
        
	MetaData md = new MetaData();
//	md.setName(rootNode.get("name").asText());
        md.setName(map.get("name"));
	md.setPlatform("TinyVersus");
        md.setDesc(map.get("description"));
	
        String id = tokenId.toString();
	md.setId(id);
        md.setNumber(id);
        
        String imageUrl = map.get("image");

	md.setImageUrl(imageUrl);
//	md.setImageUrl(imageNode.asText());
	
	return md;
    }
}
