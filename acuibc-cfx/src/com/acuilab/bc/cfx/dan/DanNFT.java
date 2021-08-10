package com.acuilab.bc.cfx.dan;

import com.acuilab.bc.cfx.*;
import com.acuilab.bc.main.nft.MetaData;
import com.acuilab.bc.main.util.Constants;
import com.fasterxml.jackson.databind.JsonNode;
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
public class DanNFT extends AbstractNFT {
    
    public static final String CONTRACT_ADDRESS = "cfx:accfdhh6tjwupeg0dsrjnkz1dbja2haz3astzmp1pg";
    public static final String WEBSITE = "https://dan.finance/";

    @Override
    public void init() {
    }

    @Override
    public String getName() {
	return "西游炼丹";
    }

    @Override
    public String getSymbol() {
	return "Dan";
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
        return ImageUtilities.loadImageIcon("/resource/dan" + size + ".png", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/dan" + size + ".png", true);
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
	System.out.println("json======================================" + json);
        /*
        {
                "image": "https://nft-guguo.oss-cn-hongkong.aliyuncs.com/image/85d9f1639485.png",
                "localization": {
                        "uri": "https://guguo.io:8133/api/ferc1155/i18n/6/{locale}.json",
                        "default": "en-us",
                        "locales": ["en-us", "zh-cn"]
                },
                "properties": null,
                "name": "Yellow Emperor",
                "description": "The Yellow Emperor is one of the legendary Chinese sovereigns and culture heroes included among the mytho-historical Three Sovereigns and Five Emperors.",
                "artist": null,
                "artistIntroduction": null,
                "concept": null
        }
        */
        String id16 = tokenId.toString(16);
        
	ObjectMapper mapper = new ObjectMapper();
        
        JsonNode rootNode = mapper.readTree(new URL(StringUtils.replace(json, "{id}", id16)));
        JsonNode imageNode = rootNode.get("image");
//        JsonNode localizationNode = rootNode.get("localization");
        /*
            {
                    "image": "https://public-files-one.s3.ap-northeast-1.amazonaws.com/image/b28545b2215a.png",
                    "localization": {
                            "uri": "https://dan.finance:8163/api/DCRCL1155/i18n/1/{locale}.json",
                            "default": "zh-cn",
                            "locales": ["zh-cn", "en-us"]
                    },
                    "properties": null,
                    "name": "玄奘法师",
                    "description": "灵通本讳号金蝉，只为无心听佛讲，转托尘凡苦受磨，降生世俗遭罗网。投胎落地就逢凶，未出之前临恶党。父是海州陈状元，外公总管当朝长。出身命犯落江星，顺水随波逐浪泱。海岛金山有大缘，迁安和尚将他养。年方十八认亲娘，特赴京都求外长。总管开山调大军，洪州剿寇诛凶党。状元光蕊脱天罗，子父相逢堪贺奖。复谒当今受主恩，凌烟阁上贤名响。恩官不受愿为僧，洪福沙门将道访。小字江流古佛儿，法名唤做陈玄奘。",
                    "artist": "蒋大喵",
                    "artistIntroduction": null,
                    "concept": null
            }
        */
//        JsonNode uriNode = localizationNode.get("uri");
//        ObjectMapper mapper2 = new ObjectMapper();
//	Map<String, String> map = mapper2.readValue(new URL(StringUtils.replace(uriNode.asText(), "{locale}", "zh-cn")), Map.class);
//	MetaData md = new MetaData();
//	md.setName(map.get("name"));
//	md.setPlatform("炼丹西游");
//        md.setDesc(map.get("description"));

	MetaData md = new MetaData();
	md.setName(rootNode.get("name").asText());
	md.setPlatform("炼丹西游");
        md.setDesc(rootNode.get("description").asText());
	
        String id = tokenId.toString();
	md.setId(id);
        md.setNumber(id);
	md.setImageUrl(imageNode.asText());
	
	return md;
    }
    
}
