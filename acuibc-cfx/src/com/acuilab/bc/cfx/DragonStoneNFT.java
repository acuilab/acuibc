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
 * 龙石合约
 */
public class DragonStoneNFT extends AbstractNFT {
    
    public static final String CONTRACT_ADDRESS = "cfx:aca3rsc5dexn0v0d65gbzuuvdkygmby54a0937ab68";
    public static final String WEBSITE = "https://condragon.com/";
     
    @Override
    public void init() {
    }

    @Override
    public String getName() {
	return "DragonStone";
    }

    @Override
    public String getSymbol() {
	return "DragonStone";
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
        return ImageUtilities.loadImageIcon("/resource/dragonstone" + size + ".png", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/dragonstone" + size + ".png", true);
    }
    
    @Override
    public MetaData getMetaData(BigInteger tokenId) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
	ContractCall contract = new ContractCall(cfx, new Address(CONTRACT_ADDRESS));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
//        String value = contract.call("uri", new org.web3j.abi.datatypes.Uint(tokenId)).sendAndGet();
//        String json = DecodeUtil.decode(value, org.web3j.abi.datatypes.Utf8String.class);
        
        MetaData md = new MetaData();
        
        String id = tokenId.toString();
	md.setId(id);
        md.setNumber(id);

        //没有uri，直接写图片
        md.setImage(ImageUtilities.loadImage("/resource/dragonstone.png", true));
        
        md.setDesc("3条C级龙换一个龙石，龙石随机抽高级龙");
        md.setName("龙石NFT");
	md.setPlatform("ConDragon");
	
	return md;
    }
}
