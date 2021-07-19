package com.acuilab.bc.cfx;

import com.acuilab.bc.main.nft.MetaData;
import com.acuilab.bc.main.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import conflux.web3j.Account;
import conflux.web3j.Cfx;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.contract.abi.DecodeUtil;
import conflux.web3j.types.Address;
import java.awt.Image;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;
import javax.swing.Icon;
import org.apache.commons.lang3.StringUtils;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
//import org.apache.batik.transcoder;

/**
 *
 * @author admin
 */
public class CnsNFT extends AbstractNFT {

    public static final String CONTRACT_ADDRESS = "cfx:acdzztjdv0vcx2z2znt296y6sd4hujswean18n0gxy";
    public static final String WEBSITE = "https://trustdomains.org/";

    @Override
    public void init() {
    }

    @Override
    public String getName() {
        return "域名";
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
        ContractCall contract = new ContractCall(cfx, new Address(CONTRACT_ADDRESS));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        //System.out.println("tokneId:"+tokenId);

        String value = contract.call("tokenURI", new org.web3j.abi.datatypes.generated.Uint256(tokenId)).sendAndGet();
        String json = DecodeUtil.decode(value, org.web3j.abi.datatypes.Utf8String.class);
        
        MetaData md = new MetaData();
        String id = tokenId.toString();
        md.setId(id);
        md.setNumber(id);
        
	ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = mapper.readValue(new URL(json), Map.class);

        String imageData = map.get("image_data");
        
        Image image = ImageUtilities.loadImage("/resource/cns178.png", true);
        
        //Image image = svgToPng(imageData, 1.0f);
                
        md.setImage(image);
        
        md.setDesc(map.get("description"));
        md.setName(map.get("name"));
	md.setPlatform("Trust Domains");
        
        return md;
    }

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
    public String safeTransferFrom(String privateKey, String from, String to, BigInteger tokenId, String data, BigInteger gas) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
	
        Account account = Account.create(cfx, privateKey);
	return account.call(new Account.Option().withGasPrice(gas).withGasLimit(this.gasLimit()), new Address(CONTRACT_ADDRESS), "safeTransferFrom", 
            new Address(from).getABIAddress(), 
	    new Address(to).getABIAddress(), 
	    new org.web3j.abi.datatypes.Uint(tokenId), 
	    new org.web3j.abi.datatypes.DynamicBytes(StringUtils.getBytes(data, Charset.forName("UTF-8"))));
    }
    
//    public InputStream svgToPng(String originFile, Float multiple) throws IOException, TranscoderException {
//        if (originFile != null && multiple != null) {
//            long st = System.currentTimeMillis();
//            ByteArrayOutputStream pngStream = new ByteArrayOutputStream();
//            InputStream pngInput;
//                byte[] bytes = originFile.getBytes("utf-8");
//                TranscoderInput input = new TranscoderInput(new ByteArrayInputStream(bytes));
//                PNGTranscoder t = new PNGTranscoder();
//          //获取宽高，动态放大
//                Float width = svgSize(originFile, "width");
//                Float height = svgSize(originFile, "height");
//                t.addTranscodingHint(PNGTranscoder.KEY_WIDTH, width * multiple);
//                t.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, height * multiple);
//          //格式转换
//                t.transcode(input, new TranscoderOutput(pngStream));
//                byte[] outByte = pngStream.toByteArray();
//                pngInput = new ByteArrayInputStream(outByte);
//                long cost = System.currentTimeMillis() - st;
//                log.info("SvgToPng success, cost time {} ms", cost);
//                    pngStream.close();
//            return pngInput;
//        } else {
//            return null;
//        }
//    }
 


}
