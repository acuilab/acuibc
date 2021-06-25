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
public class SongSecretNFT extends AbstractNFT {
    
    public static final String CONTRACT_ADDRESS = "CFX:TYPE.CONTRACT:ACBPNMW6A31AT565P5J1R489NTYZ5C7A5YAFH7V53U";
    public static final String WEBSITE = "";
     
    @Override
    public void init() {
    }

    @Override
    public String getName() {
	return "SONG'S SECRET";
    }

    @Override
    public String getSymbol() {
	return "SONG'S SECRET";
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
        return ImageUtilities.loadImageIcon("/resource/song" + size + ".jpg", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/song" + size + ".jpg", true);
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
//    "token_id":"1",
//    "image":"http://baihuo.fr/SONG.mp4",
//    "ipfs":"https://ipfs.io/ipfs/QmfT8x9ChPHNLTR6DN59ytE3mrCBtLDgvGfJBpUKqoXkKF",
//    "dweb.link":"https://bafybeih6iami5e3rz2uam4dgex44343nhyebkdemkc2ftlxopyoitpstxy.ipfs.dweb.link",
//    "name":"松交所绝密收藏",
//    "description":""
//}

        String id = tokenId.toString();
	md.setId(id);
	
	ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(new URL(json), Map.class);

        //视频截图
        md.setImage(ImageUtilities.loadImage("/resource/songsecret.jpg", true));
        md.setDesc("密");
        md.setName(map.get("name"));
	md.setPlatform("Song");
        md.setNumber(map.get("token_id"));
	
	return md;
    }
}
