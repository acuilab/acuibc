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
public class MinerNFT extends AbstractNFT {
    
    public static final String CONTRACT_ADDRESS = "cfx:achmupumcabzu59dj90nn400uppgy4gf2u1775528k";
    public static final String WEBSITE = "http://nft.tspace.io";

    @Override
    public void init() {
    }

    @Override
    public String getName() {
	return "Miner";
    }

    @Override
    public String getSymbol() {
	return "Miner";
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
        String number = map.get("number");
        String type = map.get("type");
       
        md.setId(number);
        md.setName("Miner NFT");
	md.setDesc("Conflux创世挖矿4300名矿工纪念NFT。Conflux基金会与Tspace真情合作，为所有参与测试网络挖矿的矿工铸造了专属NFT证书，来纪念他们的卓越贡献。他们的价值如同钻石，历经岁月的窖藏与打磨，方显闪耀璀璨，得万人追捧，一颗恒久远。");
                
        md.setImage(ImageUtilities.loadImage("/resource/Miner178.png", true));
		
        switch(type)
        {
            case "0":
                md.setName("炭治狸矿工");
                md.setImage(ImageUtilities.loadImage("/resource/01_KamadoTanjirouMiner.jpg", true));
                md.setDesc("炭治狸矿工，为了纪念Conflux网络在测试阶段成功跻身世界第三大去中心化网络，Conflux社区为参加测试阶段挖矿的5405位矿工铸造了专属NFT，这些NFT的设计来自社区优秀的设计师，以彰显他们的卓越贡献。");
                break;
            case "1":
                md.setName("炫彩矿工");
                md.setImage(ImageUtilities.loadImage("/resource/02_DazzlingMiner.jpg", true));
                md.setDesc("炫彩矿工, 为了纪念Conflux网络在测试阶段成功跻身世界第三大去中心化网络，Conflux社区为参加测试阶段挖矿的5405位矿工铸造了专属NFT，这些NFT的设计来自社区优秀的设计师，以彰显他们的卓越贡献。 ");
                break;
            case "2":
                md.setName("烤瓜矿工");
                md.setImage(ImageUtilities.loadImage("/resource/03_ConFiWatermelon.png", true));
                md.setDesc("烤瓜矿工, 为了纪念Conflux网络在测试阶段成功跻身世界第三大去中心化网络，Conflux社区为参加测试阶段挖矿的5405位矿工铸造了专属NFT，这些NFT的设计来自社区优秀的设计师，以彰显他们的卓越贡献。 ");
                break;
            case "3":
                md.setName("黄金矿工");
                md.setImage(ImageUtilities.loadImage("/resource/04_GoldenMiner.jpg", true));
                md.setDesc("黄金矿工, 为了纪念Conflux网络在测试阶段成功跻身世界第三大去中心化网络，Conflux社区为参加测试阶段挖矿的5405位矿工铸造了专属NFT，这些NFT的设计来自社区优秀的设计师，以彰显他们的卓越贡献。 ");
                break;
            case "4":
                md.setName("哆啦A梦矿工");
                md.setImage(ImageUtilities.loadImage("/resource/05_DoraemonMiner.jpg", true));
                md.setDesc("哆啦A梦矿工, 为了纪念Conflux网络在测试阶段成功跻身世界第三大去中心化网络，Conflux社区为参加测试阶段挖矿的5405位矿工铸造了专属NFT，这些NFT的设计来自社区优秀的设计师，以彰显他们的卓越贡献。 ");
                break;
            case "5":
                md.setName("流金岁月矿工");
                md.setImage(ImageUtilities.loadImage("/resource/06_GoldenTimesMiner.png", true));
                md.setDesc("流金岁月矿工, 为了纪念Conflux网络在测试阶段成功跻身世界第三大去中心化网络，Conflux社区为参加测试阶段挖矿的5405位矿工铸造了专属NFT，这些NFT的设计来自社区优秀的设计师，以彰显他们的卓越贡献。 ");
                break;
            case "6":
                md.setName("六度空间矿工");
                md.setImage(ImageUtilities.loadImage("/resource/07_SixDegreesSeparation.jpg", true));
                md.setDesc("六度空间矿工, 为了纪念Conflux网络在测试阶段成功跻身世界第三大去中心化网络，Conflux社区为参加测试阶段挖矿的5405位矿工铸造了专属NFT，这些NFT的设计来自社区优秀的设计师，以彰显他们的卓越贡献。 ");
                break;
            
            case "7":
                md.setName("一夜暴富矿工");
                md.setImage(ImageUtilities.loadImage("/resource/08_TodamoonMiner.jpg", true));
                md.setDesc("一夜暴富矿工，为了纪念Conflux网络在测试阶段成功跻身世界第三大去中心化网络，Conflux社区为参加测试阶段挖矿的5405位矿工铸造了专属NFT，这些NFT的设计来自社区优秀的设计师，以彰显他们的卓越贡献。");
                break;
            case "8":
                md.setName("主网纪念（金）矿工");
                md.setImage(ImageUtilities.loadImage("/resource/09_ConfluxMinerGoden.png", true));
                md.setDesc("主网纪念（金）矿工，为了纪念Conflux网络在测试阶段成功跻身世界第三大去中心化网络，Conflux社区为参加测试阶段挖矿的5405位矿工铸造了专属NFT，这些NFT的设计来自社区优秀的设计师，以彰显他们的卓越贡献。");
                break;
            case "9":
                md.setName("主网纪念（银）矿工");
                md.setImage(ImageUtilities.loadImage("/resource/10_ConfluxMinerSilver.png", true));
                md.setDesc("主网纪念（银）矿工，为了纪念Conflux网络在测试阶段成功跻身世界第三大去中心化网络，Conflux社区为参加测试阶段挖矿的5405位矿工铸造了专属NFT，这些NFT的设计来自社区优秀的设计师，以彰显他们的卓越贡献。");
                break;
            case "10":
                md.setName("非洲将军矿工");
                md.setImage(ImageUtilities.loadImage("/resource/11_AfricanGeneralMiner.jpg", true));
                md.setDesc("非洲将军矿工，为了纪念Conflux网络在测试阶段成功跻身世界第三大去中心化网络，Conflux社区为参加测试阶段挖矿的5405位矿工铸造了专属NFT，这些NFT的设计来自社区优秀的设计师，以彰显他们的卓越贡献。");
                break;
            case "11":
                md.setName("梦幻之星矿工");
                md.setImage(ImageUtilities.loadImage("/resource/12_PhantasyStarMiner.jpg", true));
                md.setDesc("梦幻之星矿工，为了纪念Conflux网络在测试阶段成功跻身世界第三大去中心化网络，Conflux社区为参加测试阶段挖矿的5405位矿工铸造了专属NFT，这些NFT的设计来自社区优秀的设计师，以彰显他们的卓越贡献。");
                break;
            case "12":
                md.setName("3D矿工");
                md.setImage(ImageUtilities.loadImage("/resource/13_3DMiner.png", true));
                md.setDesc("3D矿工，为了纪念Conflux网络在测试阶段成功跻身世界第三大去中心化网络，Conflux社区为参加测试阶段挖矿的5405位矿工铸造了专属NFT，这些NFT的设计来自社区优秀的设计师，以彰显他们的卓越贡献。");
                break;
            case "13":
                md.setName("热带风情矿工");
                md.setImage(ImageUtilities.loadImage("/resource/14_TropicalMiner.png", true));
                md.setDesc("热带风情矿工，为了纪念Conflux网络在测试阶段成功跻身世界第三大去中心化网络，Conflux社区为参加测试阶段挖矿的5405位矿工铸造了专属NFT，这些NFT的设计来自社区优秀的设计师，以彰显他们的卓越贡献。");
                break;
            case "14":
                md.setName("ART101新锐矿工");
                md.setImage(ImageUtilities.loadImage("/resource/15_TheART101AggressiveMiner.png", true));
                md.setDesc("ART101新锐矿工，为了纪念Conflux网络在测试阶段成功跻身世界第三大去中心化网络，Conflux社区为参加测试阶段挖矿的5405位矿工铸造了专属NFT，这些NFT的设计来自社区优秀的设计师，以彰显他们的卓越贡献。");
                break;
            case "15":
                md.setName("金属狂潮矿工");
                md.setImage(ImageUtilities.loadImage("/resource/16_MetalFrenzyMiner.jpg", true));
                md.setDesc("金属狂潮矿工，为了纪念Conflux网络在测试阶段成功跻身世界第三大去中心化网络，Conflux社区为参加测试阶段挖矿的5405位矿工铸造了专属NFT，这些NFT的设计来自社区优秀的设计师，以彰显他们的卓越贡献。");
                break;
            case "16":
                md.setName("ART101豪华矿工");
                md.setImage(ImageUtilities.loadImage("/resource/17_TheART101DeluxeMiner.jpg", true));
                md.setDesc("ART101豪华矿工，为了纪念Conflux网络在测试阶段成功跻身世界第三大去中心化网络，Conflux社区为参加测试阶段挖矿的5405位矿工铸造了专属NFT，这些NFT的设计来自社区优秀的设计师，以彰显他们的卓越贡献。");
                break;
            
            default:
                break;

        }
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
            if(id.compareTo(BigInteger.valueOf(1154)) > 0 && id.compareTo(BigInteger.valueOf(6559)) <= 0) {
                list.add(id);
            }
        }
        
        return list.toArray(new BigInteger[] {});
    }
}
