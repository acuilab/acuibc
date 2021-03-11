package com.acuilab.bc.cfx;

import com.acuilab.bc.main.nft.MetaData;
import com.acuilab.bc.main.util.Constants;
import conflux.web3j.Cfx;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.contract.abi.DecodeUtil;
import conflux.web3j.types.Address;
import java.awt.Image;
import java.math.BigInteger;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

/**
 *
 * @author admin
 */
public class ConfluxGuardianNFT extends AbstractNFT {
    
    public static final String CONTRACT_ADDRESS = "cfx:ach7c9fr2skv5fft98cygac0g93999z1refedecnn1";
    public static final String WEBSITE = "https://fccfx.confluxscan.io/";

    @Override
    public void init() {
    }

    @Override
    public String getName() {
	return "Conflux Guardian NFT";
    }

    @Override
    public String getSymbol() {
	return "CG";
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
        return ImageUtilities.loadImageIcon("/resource/ConfluxGuardian" + size + ".png", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/ConfluxGuardian" + size + ".png", true);
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
        System.out.println(value.toString());
        System.out.println(json.toString());
        
	String id = tokenId.toString();
	md.setId(id);
	md.setImageUrl("https://cdn.img.imakejoy.com/guardian/nft.png");
        md.setDesc("累计质押满1000FC的用户将获得一个NFT作为纪念徽章。");
        md.setName("守护者徽章");
	md.setPlatform("Conflux");
	
	return md;
    }
}
