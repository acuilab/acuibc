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
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
//import org.apache.batik.transcoder;

/**
 *
 * @author admin
 */
public class OreoNFT extends AbstractNFT {

    public static final String CONTRACT_ADDRESS = "cfx:acg65as4aawku4a3rdr5jewuv187m09pkjgbsd62b1";
    public static final String WEBSITE = "https://www.sohu.com/a/496017730_228864";

    @Override
    public void init() {
    }

    @Override
    public String getName() {
        return "OreoNFT";
    }

    @Override
    public String getSymbol() {
        return "OreoNFT";
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
        return ImageUtilities.loadImageIcon("/resource/oreonft" + size + ".png", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/oreonft" + size + ".png", true);
    }

    @Override
    public MetaData getMetaData(BigInteger tokenId) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        ContractCall contract = new ContractCall(cfx, new Address(CONTRACT_ADDRESS));

        String value = contract.call("tokenURI", new org.web3j.abi.datatypes.generated.Uint256(tokenId)).sendAndGet();
        String json = DecodeUtil.decode(value, org.web3j.abi.datatypes.Utf8String.class);
        
        MetaData md = new MetaData();
        String id = tokenId.toString();
        md.setId(id);
        md.setNumber(id);
        
	ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(new URL(json), Map.class);
        
        md.setName(map.get("name"));
        md.setPlatform("奥利奥");
        md.setDesc(map.get("description"));
        
        String imageUrl = map.get("image");
	md.setImageUrl(imageUrl);
        
        return md;
    }

    @Override
    public BigInteger[] tokensOf(String address) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        ContractCall contract = new ContractCall(cfx, new Address(CONTRACT_ADDRESS));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type

        String balanceStr = contract.call("balanceOf", new Address(address).getABIAddress()).sendAndGet();

        int balanceDecode = DecodeUtil.decode(balanceStr, org.web3j.abi.datatypes.Uint.class).intValue();

        if (balanceDecode <= 0) {
            return new BigInteger[0];
        }
        //System.out.println("Balance:"+balanceDecode);
        BigInteger[] ret = new BigInteger[balanceDecode];

        for (int i = 0; i < balanceDecode; i++) {
            String tokenOfOwnerByIndexStr = contract.call("tokenOfOwnerByIndex", new Address(address).getABIAddress(), new org.web3j.abi.datatypes.generated.Uint256(BigInteger.valueOf(i))).sendAndGet();
            ret[i] = DecodeUtil.decode(tokenOfOwnerByIndexStr, org.web3j.abi.datatypes.generated.Uint256.class);
        }

        return ret;
    }
    
    @Override
    public Type getType() {
        return Type.NFT721;
    }
}
