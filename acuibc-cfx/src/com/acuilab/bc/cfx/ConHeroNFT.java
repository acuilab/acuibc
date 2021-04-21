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
public class ConHeroNFT extends AbstractNFT {
    
    public static final String CONTRACT_ADDRESS = "cfx:acgjttbz35rukntbvnp6u6arx8rwwxekfyks00vr3n";
    public static final String WEBSITE = "https://conhero.com/";
     
    @Override
    public void init() {
    }

    @Override
    public String getName() {
	return "ConHero";
    }

    @Override
    public String getSymbol() {
	return "ConHero";
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
        return ImageUtilities.loadImageIcon("/resource/conhero" + size + ".png", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/conhero" + size + ".png", true);
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
        
        //System.out.println(json);
        
        MetaData md = new MetaData();
	
//token_id	"1338"
//image	"https://cdn.image.htlm8.top/conhero/flame-lord.png"
//description	"ConHero是Conflux生态首款即时PK挖…ConHero NFT还可以质押挖矿、开宝箱。"
//description_en	"ConHero is the first RTS Mining game in the Conflux ecosystem. Players can use NFT to fight, Rank, and participate in PK competitions in the game. Players can also use ConHero NFT to stake mining and open treasure boxes.\n\nAt the same time, the HeroNFT bli"
//name	"烈焰君主"
//name_en	"Flame Lord"

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
	md.setPlatform("ConHero");
        md.setNumber(map.get("token_id"));
	
	return md;
    }
}
