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
public class ConDragonNFT extends AbstractNFT {
    
    public static final String CONTRACT_ADDRESS = "cfx:acb3fcbj8jantg52jbg66pc21jgj2ud02pj1v4hkwn";
    public static final String WEBSITE = "https://condragon.com/";
     
    @Override
    public void init() {
    }

    @Override
    public String getName() {
	return "恐龙";
    }

    @Override
    public String getSymbol() {
	return "ConDragon";
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
        return ImageUtilities.loadImageIcon("/resource/condragon" + size + ".png", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/condragon" + size + ".png", true);
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
        md.setNumber(id);
	
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
}
