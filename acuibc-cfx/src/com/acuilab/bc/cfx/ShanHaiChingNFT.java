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
public class ShanHaiChingNFT extends AbstractNFT {
    
    public static final String CONTRACT_ADDRESS = "cfx:accyby39bahapc9gm7utnyve8j85htsf5j9173yt3r";
    public static final String WEBSITE = "http://boxnft.io/";
     
    @Override
    public void init() {
    }

    @Override
    public String getName() {
	return "山海经";
    }

    @Override
    public String getSymbol() {
	return "ShanHaiChing";
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
        return ImageUtilities.loadImageIcon("/resource/ShanHaiChing" + size + ".png", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/ShanHaiChing" + size + ".png", true);
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
        
        MetaData md = new MetaData();
	
//{
//  "name":"合体诸怀",
//  "description":"北岳的山，山中有一种野兽，它的形状像牛，四角，人的眼睛、猪的耳朵，它的名字叫各怀，他的声音如鸣雁，喜欢吃人",
//  "image":"https://metadata.boxnft.io/sh/11.jpg",
//  "attributes":null,
//  "properties":
//    {
//      "preview_file":
//                    {
//                      "type":"string",
//                      "description":"https://metadata.boxnft.io/sh/11.jpg"
//                    },
//     "preview_file2":
//                    {
//                      "type":"string",
//                      "description":"https://metadata.boxnft.io/sh/11.mp4"
//                    },
//      "preview_file2_type":
//                    {
//                      "type":"mimeType",
//                      "description":"video/mp4"
//                    }
//    }
//}

        String id = tokenId.toString();
	md.setId(id);
        
	ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(new URL(json), Map.class);

        String imageUrl = map.get("image");
        //String imageUrlSlim = StringUtils.replace(imageUrl, "\\", "");
        //System.out.println(imageUrl);
	md.setImageUrl(imageUrl);
        md.setDesc(map.get("description"));
        md.setName(map.get("name"));
	md.setPlatform("BOXNFT");
        md.setNumber(id);
	
	return md;
    }
}
