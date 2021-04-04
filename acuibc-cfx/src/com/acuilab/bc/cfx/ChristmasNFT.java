package com.acuilab.bc.cfx;

import com.acuilab.bc.main.nft.MetaData;
import com.acuilab.bc.main.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import conflux.web3j.Cfx;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.contract.abi.DecodeUtil;
import conflux.web3j.types.Address;
import java.awt.Image;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

/**
 *
 * @author admin
 */
public class ChristmasNFT extends AbstractNFT {
    
    public static final String CONTRACT_ADDRESS = "cfx:achmupumcabzu59dj90nn400uppgy4gf2u1775528k";
    public static final String WEBSITE = "http://nft.tspace.io";

    @Override
    public void init() {
    }

    @Override
    public String getName() {
	return "Christmas";
    }

    @Override
    public String getSymbol() {
	return "Christmas";
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
        return ImageUtilities.loadImageIcon("/resource/tspace" + size + ".png", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/tspace" + size + ".png", true);
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
	//md.setName("Activity NFT");
	md.setPlatform("Tspace");
	
	String id = tokenId.toString();
	md.setId(id);
	
	ObjectMapper mapper = new ObjectMapper();
	Map<String, String> map = mapper.readValue(StringUtils.substringAfter(json, id), Map.class);
        
	
	String symbol = map.get("nftSymbol");
       
        md.setName("烤仔圣诞NFT");
        md.setDesc("烤仔社区圣诞节活动发的NFT。");
        Image image = ImageUtilities.loadImage("/resource/ChristmasNFT178.png", true);
        md.setImage(image);
        
        md.setId("" + (NumberUtils.toInt(id)-476));
	
	//md.setImageUrl("http://cdn.tspace.online/Cotton/cotton.png");
	//String title = map.get("title");
	//md.setImageUrl("http://cdn.tspace.online/image/finish/" + map.get("url"));
	
	
	return md;
    }
    
    @Override
    public BigInteger[] tokensOf(String address) {
        BigInteger[] ids = super.tokensOf(address); //To change body of generated methods, choose Tools | Templates.
        List<BigInteger> list = Lists.newArrayList();
        for(BigInteger id : ids) {
            if(id.compareTo(BigInteger.valueOf(476)) > 0 && id.compareTo(BigInteger.valueOf(954)) <= 0) {
                list.add(id);
            }
        }
        
        return list.toArray(new BigInteger[] {});
    }
}
