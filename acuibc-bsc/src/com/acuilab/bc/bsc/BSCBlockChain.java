package com.acuilab.bc.bsc;

import java.util.logging.Logger;
import com.acuilab.bc.main.BlockChain;
import com.acuilab.bc.main.util.Constants;
import java.awt.Image;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import javax.swing.Icon;
import org.apache.commons.lang3.StringUtils;
import org.javatuples.Pair;
import org.openide.util.ImageUtilities;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Numeric;
import party.loveit.bip44forjava.utils.Bip44Utils;

/**
 *
 * @author admin
 */
public class BSCBlockChain implements BlockChain {
    
    private static final Logger LOG = Logger.getLogger(BSCBlockChain.class.getName());
    
    public static final String DEFAULT_NODE = "https://bsc-dataseed1.binance.org:443"; // 默认结点地址
//    public static final String DEFAULT_NODE = "https://data-seed-prebsc-1-s1.binance.org:8545"; // 默认结点地址
    public static final String BIP44PATH = "m/44'/714'/0'/0/0";  // 通用的以太坊基于bip44协议的助记词路径
    
    public static final String TRANSACTIONS_DETAIL_URL = "https://testnet.bscscan.com/tx/";

    private Admin admin;    // Admin连接
    
    public Admin getAdmin() {
        return admin;
    }
    
    @Override
    public void setDefaultNode() {
	setNode(DEFAULT_NODE);
    }

    @Override
    public void setNode(String node) {
        if(admin != null) {
            admin.shutdown();
        }
        admin = Admin.build(new HttpService(node));
    }

    @Override
    public String getDefaultNode() {
        return DEFAULT_NODE;
    }

    @Override
    public String getBIP44Path() {
        return BIP44PATH;
    }

    @Override
    public void close() {
	if(admin != null) {
	    admin.shutdown();
	}
    }

    @Override
    public String getName() {
        return "Binance Smart Chain(开发中勿用)";
    }

    @Override
    public String getSymbol() {
        return Constants.BSC_BLOCKCHAIN_SYMBAL;
    }

    @Override
    public Icon getIcon(int size) {
        return ImageUtilities.loadImageIcon("/resource/bsc" + size + ".png", true);
    }
    

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/bsc" + size + ".png", true);
    }

    @Override
    public boolean isValidAddress(String address) {
        return WalletUtils.isValidAddress(address);
    }

    @Override
    public String getTransactionDetailUrl(String hash) {
        return TRANSACTIONS_DETAIL_URL + hash;
    }

    /**
     * 通过助记词创建钱包
     * @param mnemonicWords
     * @return 返回地址和私钥对
     */
    @Override
    public Pair<String, String> createWalletByMnemonic(List<String> mnemonicWords) {
        // 1 根据助记词生成私钥
        BigInteger pathPrivateKey = Bip44Utils.getPathPrivateKey(mnemonicWords, BIP44PATH);
        
        ECKeyPair ecKeyPair = ECKeyPair.create(pathPrivateKey);
//        String publicKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey());
        String privateKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPrivateKey());
	String address = "0x" + Keys.getAddress(ecKeyPair);
        return new Pair<>(address, privateKey);
    }

    /**
     * 通过私钥导入钱包
     * @param privateKey
     * @return 返回钱包地址
     */
    @Override
    public String importWalletByPrivateKey(String privateKey) {
	Credentials credentials = Credentials.create(privateKey);
	return credentials.getAddress();
    }

    /**
     * 通过助记词导入钱包
     * @param mnemonic
     * @return 
     */
    @Override
    public Pair<String, String> importWalletByMnemonic(String mnemonic) {
	return createWalletByMnemonic(Arrays.asList(StringUtils.split(mnemonic, " ")));
    }

    @Override
    public TransactionStatus getTransactionStatusByHash(String hash) throws Exception {
	// TODO:
        return TransactionStatus.SUCCESS;
    }
    
    @Override
    public String getAddressFromDomain(String ens) {
        
        return ens;
    }
    
}
