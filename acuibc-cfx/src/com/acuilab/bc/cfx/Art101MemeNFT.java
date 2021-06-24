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
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import org.apache.commons.lang3.StringUtils;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

/**
 *
 * @author admin
 */
public class Art101MemeNFT extends AbstractNFT {
    
    public static final String CONTRACT_ADDRESS = "cfx:aca85nrag5nvadv5jungjstbcw4s23srcu7f0r2pbt";
    public static final String WEBSITE = "http://nft.tspace.io";

    @Override
    public void init() {
    }

    @Override
    public String getName() {
	return "art101MeMe";
    }

    @Override
    public String getSymbol() {
	return "Art101Meme";
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
	
	
	String id = tokenId.toString();
	md.setId(id);
        md.setNumber(id);
        
        
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(StringUtils.substringAfter(json, id), Map.class);
        //Map<String, String> map = mapper.readValue(new URL(json), Map.class);

        md.setDesc("好玩委员会和Art101发行的趣味烤仔NFT");
        md.setName("Art101Meme NFT");
	md.setPlatform("Tspace&Art101");
        if(tokenId.intValue()<21){
            md.setImageUrl("http://cdn.tspace.online/ConFiArt/meme/conStarPeople.jpeg");
            md.setDesc("");
        }else if(tokenId.intValue()<41){
            md.setImageUrl("http://cdn.tspace.online/ConFiArt/meme/tangkai.jpeg");
            md.setDesc("");
        }else if(tokenId.intValue()<61){
            md.setImageUrl("http://cdn.tspace.online/ConFiArt/meme/protectLove.jpeg");
            md.setDesc("");
        }else if(tokenId.intValue()<81){
            md.setImageUrl("http://cdn.tspace.online/ConFiArt/meme/conStarPeople.jpeg");
            md.setDesc("");
        }else if(tokenId.intValue()<101){
            //如果能展示视频，则使用下面这个视频。
            //md.setImageUrl("http://cdn.tspace.online/ConFiArt/meme/AttackingAstronauts.mp4"); 
            md.setImage(ImageUtilities.loadImage("/resource/art101meme.png", true));
            md.setDesc("");
        }else if(tokenId.intValue()<121){
            md.setImageUrl("http://cdn.tspace.online/ConFiArt/meme/saleornot.jpg");
            md.setDesc("Think of tsundere attribute and MeMe slogan, so there is the word Not for Sale and the mysterious price of pineapple headset");
        }else if(tokenId.intValue()<141){
            md.setImageUrl("http://cdn.tspace.online/ConFiArt/meme/xuancai.jpg");
            md.setDesc("COET & Meme co-titled excellent work");
        }else if(tokenId.intValue()<161){
            md.setImageUrl("http://cdn.tspace.online/ConFiArt/meme/niceday.png");
            md.setDesc("");
        }else if(tokenId.intValue()<181){
            md.setImageUrl("http://cdn.tspace.online/ConFiArt/meme/starrynight.jpg");
            md.setDesc("");
        }else if(tokenId.intValue()<201){
            md.setImageUrl("http://cdn.tspace.online/ConFiArt/meme/SpongeBob.jpeg");
            md.setDesc("");
        }
        // 无友好的metadata，不支持不同的desc了
        md.setDesc("好玩委员会和Art101发行的趣味烤仔NFT");
	return md;
    }
    
    @Override
    public BigInteger[] tokensOf(String address) {
        BigInteger[] ids = super.tokensOf(address); //To change body of generated methods, choose Tools | Templates.
        List<BigInteger> list = Lists.newArrayList();
        for(BigInteger id : ids) {
            if(id.compareTo(BigInteger.valueOf(0)) > 0 && id.compareTo(BigInteger.valueOf(200)) <= 0) {
                list.add(id);
            }
        }
        
        return list.toArray(new BigInteger[] {});
    }
}
