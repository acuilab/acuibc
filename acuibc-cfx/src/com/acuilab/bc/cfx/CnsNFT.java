package com.acuilab.bc.cfx;

import com.acuilab.bc.main.nft.MetaData;
import com.acuilab.bc.main.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import conflux.web3j.Account;
import conflux.web3j.Cfx;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.contract.abi.DecodeUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.io.File;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import org.apache.commons.lang3.StringUtils;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.DynamicArray;

/**
 *
 * @author admin
 */
public class CnsNFT extends AbstractNFT {

    public static final String CONTRACT_ADDRESS = "0x875abd038da229e2b8aadf8ff29c70f47821d220";
    public static final String WEBSITE = "https://trustdomains.org/";

    @Override
    public void init() {
    }

    @Override
    public String getName() {
        return "5 CNS NFT";
    }

    @Override
    public String getSymbol() {
        return "CNS";
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
        return ImageUtilities.loadImageIcon("/resource/cns" + size + ".png", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/cns" + size + ".png", true);
    }

    @Override
    public MetaData getMetaData(BigInteger tokenId) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        ContractCall contract = new ContractCall(cfx, CONTRACT_ADDRESS);
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        //System.out.println("tokneId:"+tokenId);

        String value = contract.call("tokenURI", new org.web3j.abi.datatypes.generated.Uint256(tokenId)).sendAndGet();
        String json = DecodeUtil.decode(value, org.web3j.abi.datatypes.Utf8String.class);

        //System.out.println(json);
        //xxx.cfx
        MetaData md = new MetaData();
        md.setName("CNS NFT");
        md.setPlatform("Trust Domains");
        md.setDesc(json);

        Image image = ImageUtilities.loadImage("/resource/cns" + "178" + ".png", true);
        //Image image = ImageIO.read(new File("/resource/cns" + "178" + ".png"));
//        Graphics2D g2d = (Graphics2D) image.getGraphics();
//        //int width = (int)(getPreferredSize().getWidth());
//        //int height = (int)(getPreferredSize().getHeight());
//        Paint oldPaint = g2d.getPaint();
//        g2d.setPaint(Color.BLACK);
//        //g2d.fillRect(0, 0, width, height);
//        //g2d.drawImage(image, 0, 0, width, height, this);
//
//        g2d.setPaint(Color.WHITE);
//        Font font = g2d.getFont();
//        Font newFont = new Font(font.getName(), Font.BOLD, font.getSize());
//
//        // 计算字体宽度和高度
//        // @see https://zhidao.baidu.com/question/685805523016097932.html
//        String content = "域名：" + json;
//        FontMetrics fm = g2d.getFontMetrics(font);
//        int fWidth = fm.stringWidth(content);
//        int fHeight = fm.getHeight();
//        g2d.setBackground(Color.BLACK);//设置背景色
//        g2d.clearRect(0, 0, fWidth, fHeight);//通过使用当前绘图表面的背景色进行填充来清除指定的矩形。
//        g2d.setFont(newFont);
//        g2d.drawString(content, 0, fm.getAscent());
//
//        g2d.setPaint(oldPaint);

        md.setImage(image);

        String id = tokenId.toString();
        md.setId(id);

//	ObjectMapper mapper = new ObjectMapper();
//	Map<String, String> map = mapper.readValue(StringUtils.substringAfter(json, id), Map.class);
//	md.setImageUrl("http://cdn.tspace.online/image/finish/" + map.get("url"));
//	String title = map.get("title");
        return md;
    }

    public BigInteger[] tokensOf(String address) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        ContractCall contract = new ContractCall(cfx, getContractAddress());
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type

        String balanceStr = contract.call("balanceOf", new org.web3j.abi.datatypes.Address(address)).sendAndGet();
        int balanceDecode = DecodeUtil.decode(balanceStr, org.web3j.abi.datatypes.Uint.class).intValue();

        if (balanceDecode <= 0) {
            return new BigInteger[0];
        }
        //System.out.println("Balance:"+balanceDecode);
        BigInteger[] ret = new BigInteger[balanceDecode];

        for (int i = 0; i < balanceDecode; i++) {
            String tokenOfOwnerByIndexStr = contract.call("tokenOfOwnerByIndex", new org.web3j.abi.datatypes.Address(address), new org.web3j.abi.datatypes.generated.Uint256(BigInteger.valueOf(i))).sendAndGet();

            ret[i] = DecodeUtil.decode(tokenOfOwnerByIndexStr, org.web3j.abi.datatypes.generated.Uint256.class);

            //System.out.println("id:" + ret[i]);
        }

        return ret;
    }
    
    public String safeTransferFrom(String privateKey, String from, String to, BigInteger tokenId, String data, BigInteger gas) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
	
        Account account = Account.create(cfx, privateKey);
	return account.call(new Account.Option().withGasPrice(gas).withGasLimit(this.gasLimit()), getContractAddress(), "safeTransferFrom", 
            new org.web3j.abi.datatypes.Address(from), 
	    new org.web3j.abi.datatypes.Address(to), 
	    new org.web3j.abi.datatypes.Uint(tokenId), 
            //new org.web3j.abi.datatypes.Uint(BigInteger.ONE), 
	    new org.web3j.abi.datatypes.DynamicBytes(StringUtils.getBytes(data, Charset.forName("UTF-8"))));
    }
}
