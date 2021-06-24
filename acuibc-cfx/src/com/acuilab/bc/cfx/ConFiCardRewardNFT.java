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
public class ConFiCardRewardNFT extends AbstractNFT {
    
    public static final String CONTRACT_ADDRESS = "cfx:acerdrbgc87wvrbfvu18sgwtdbw79wsaha851ues9m";
    public static final String WEBSITE = "https://boxnft.io/";
     
    @Override
    public void init() {
    }

    @Override
    public String getName() {
	return "烤仔卡合成";
    }

    @Override
    public String getSymbol() {
	return "ConFiCardReward";
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
        
        //烤仔卡的链接换了，需要从http改为https
        json = StringUtils.replace(json, "http:", "https:");
        
        System.out.println(json);
        
        MetaData md = new MetaData();
	
        String id = tokenId.toString();
	md.setId(id);
        md.setNumber(id);
        
	ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(new URL(json), Map.class);

        String imageUrl = map.get("image");
        //String imageUrlSlim = StringUtils.replace(imageUrl, "\\", "");
        //System.out.println(imageUrl);
	md.setImageUrl("https://images.weserv.nl/?url=" + imageUrl);
        md.setDesc(map.get("description"));
        md.setName(map.get("name"));
	md.setPlatform("BoxNFT");
        
	
	return md;
    }
}
