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
public class CottonNFT extends AbstractNFT {
    
    public static final String CONTRACT_ADDRESS = "cfx:achmupumcabzu59dj90nn400uppgy4gf2u1775528k";
    public static final String WEBSITE = "http://nft.tspace.io";

    @Override
    public void init() {
    }

    @Override
    public BigInteger[] tokensOf(String address) {
        BigInteger[] ids = super.tokensOf(address); //To change body of generated methods, choose Tools | Templates.
        List<BigInteger> list = Lists.newArrayList();
        for(BigInteger id : ids) {
            //棉花的tokenid是从955到1154的200个
            if(id.compareTo(BigInteger.valueOf(954)) > 0 && id.compareTo(BigInteger.valueOf(1154)) <= 0) {
                list.add(id);
            }
        }
        
        return list.toArray(new BigInteger[] {});
    }

    @Override
    public String getName() {
	return "新疆棉";
    }

    @Override
    public String getSymbol() {
	return "Cotton";
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
	
        //System.out.println(json);
	// 8016{"body":"000","eye":"013","nose":"001","auxiliary":"001","background":"000","clothes":"006","props":"000","title":"002_000_000_013_001_000_001_000","url":"002_000_000_013_001_000_001_000.png"}
	MetaData md = new MetaData();
	//md.setName("Activity NFT");
	md.setPlatform("Tspace");
	
	String id = tokenId.toString();
	md.setId(id);
	
	ObjectMapper mapper = new ObjectMapper();
	Map<String, String> map = mapper.readValue(StringUtils.substringAfter(json, id), Map.class);
	
	String symbol = map.get("nftSymbol");
        
        md.setName("棉花NFT");
	md.setDesc("【中本聪棉花】是由加密艺术家@XIN主创的作品，包含了神秘的比特币创始人中本聪，烤仔的宠物烤喵，顶级POW公链Conflux和它的两种链上通证FC、CFX，新疆长绒棉等一系列元素，由全世界最好的、最顶级的素材构成了整副NFT，含蓄地表达了去中心化世界的民族自信和爱国主义思潮。\n" +
"      本次公益活动，Tspace共铸造200个 【中本聪棉花】NFT，统一售价为20   wCFX（注：wCFX为CFX在Tspace平台映射的代币形式），且永不增发。值得小伙伴们注意的是，为了将加密朋克的爱国主义进行到底，凡是购买该款NFT的用户，都将获得一件同样款式的纪念T恤。\n" +
"       与此同时，Tspace 将会参与由 CottonDAO 发起的 \"行为艺术即空投\" 的活动，为所有参与这个主题创造的艺术家和参与购买爱国主义NFT的用户申请 Cotton 代币空投奖励，可在日后共同参与治理 Cotton DAO 社区。\n" +
"       最后，作为这款爱国主义NFT的主创，加密艺术家@XIN宣布此次公益活动的盈利所得将全部捐赠给慈善事业。");
        md.setImage(ImageUtilities.loadImage("/resource/Cotton178.png", true));
                
        //棉花NFT的tokenid和棉花编号对应关系略特殊，155-200号是tokenid955至999，1-154号是1001至1054.
        if(NumberUtils.toInt(id)<1000){
            md.setNumber("" + (NumberUtils.toInt(id)-955+156));
        }else{
            md.setNumber("" + (NumberUtils.toInt(id)-1000+1));
        }
	
	
	return md;
    }
}
