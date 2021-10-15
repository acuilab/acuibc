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
public class PandarianNFT extends AbstractNFT {
    
    public static final String CONTRACT_ADDRESS = "cfx:acge4gpm33d0j2nxnf9jywf6776f252mp2r74ukykh";
    public static final String WEBSITE = "https://www.tinyversus.com/";

    @Override
    public void init() {
    }

    @Override
    public String getName() {
	return "熊猫胖达";
    }

    @Override
    public String getSymbol() {
	return "Pandarian";
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
        
//	ObjectMapper mapper = new ObjectMapper();
//        
//        JsonNode rootNode = mapper.readTree(new URL(StringUtils.replace(json, "{id}", id16)));
//        JsonNode imageNode = rootNode.get("image");
        
	MetaData md = new MetaData();
//	md.setName(rootNode.get("name").asText());
        md.setName(id16);
	md.setPlatform("tinyversus");
//        md.setDesc(rootNode.get("description").asText());
	
        String id = tokenId.toString();
	md.setId(id);
        md.setNumber(id);
//	md.setImageUrl(imageNode.asText());
	
	return md;
    }
}
