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
public class ConFiBirthdayNFT extends AbstractNFT {
    
    public static final String CONTRACT_ADDRESS = "cfx:aca85nrag5nvadv5jungjstbcw4s23srcu7f0r2pbt";
    public static final String WEBSITE = "http://nft.tspace.io";

    @Override
    public void init() {
    }

    @Override
    public String getName() {
	return "烤仔生日快乐";
    }

    @Override
    public String getSymbol() {
	return "ConFiBirthday";
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
	MetaData md = new MetaData();
	
	String id = tokenId.toString();
	md.setId(id);
        
        //显示的id是tokenid减去236。
        md.setNumber("" + (tokenId.intValue()-225));
        
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(StringUtils.substringAfter(json, id), Map.class);
        //Map<String, String> map = mapper.readValue(new URL(json), Map.class);

        md.setDesc("烤仔生日快乐！2019年4月19日,由于飞船故障,烤仔迫降地球。为了支付昂贵的飞船维修费用早日返航母星,烤仔入职Conflux,成为了一名光荣的——程序员!");
        md.setName("烤仔生日快乐");
	md.setPlatform("Tspace");
        
        Image image = ImageUtilities.loadImage("/resource/ConfiAnniversary.jpeg", true);
        md.setImage(image);
        
        
	return md;
    
    }
    
    @Override
    public BigInteger[] tokensOf(String address) {
        BigInteger[] ids = super.tokensOf(address); //To change body of generated methods, choose Tools | Templates.
        List<BigInteger> list = Lists.newArrayList();
        for(BigInteger id : ids) {
            if(id.compareTo(BigInteger.valueOf(225)) > 0 && id.compareTo(BigInteger.valueOf(236)) <= 0) {
                list.add(id);
            }
        }
        
        return list.toArray(new BigInteger[] {});
    }
}
