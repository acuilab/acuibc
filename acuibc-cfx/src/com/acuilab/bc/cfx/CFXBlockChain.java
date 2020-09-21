package com.acuilab.bc.cfx;

import com.acuilab.bc.main.util.AESUtil;
import com.acuilab.bc.main.wallet.Wallet;
import conflux.web3j.Account;
import conflux.web3j.Cfx;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.DigestUtils;
import org.web3j.crypto.ECKeyPair;
import org.web3j.utils.Numeric;
import party.loveit.bip44forjava.utils.Bip44Utils;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
import com.acuilab.bc.main.BlockChain;
import conflux.web3j.response.Status;
import conflux.web3j.types.Address;
import conflux.web3j.types.AddressException;
import conflux.web3j.types.RawTransaction;
import java.awt.Image;
import java.util.Arrays;
import org.javatuples.Pair;

/**
 *
 * @author admin
 */
public class CFXBlockChain implements BlockChain {
    
    private static final Logger LOG = Logger.getLogger(CFXBlockChain.class.getName());
    
    public static final String DEFAULT_NODE = "http://mainnet-jsonrpc.conflux-chain.org:12537"; // 默认结点地址
    public static final String BIP44PATH = "m/44'/503'/0'/0/0";
    public static final String SYMBOL = "CFX";
    
    public static final String TRANSACTIONS_DETAIL_URL = "http://www.confluxscan.io/transactionsdetail/";
    
    private Cfx cfx;
    private BigInteger chainId;
    private BigInteger gasPrice;
    
    // 下面两种写法都会导致BlockChainManager无法找到CFXBlockChain
    // 所以不能声明构造函数，即使是无参构造函数
//    ①private Cfx cfx = Cfx.create(DEFAULT_NODE);
//    ②public CFXBlockChain() {
//        cfx = Cfx.create(DEFAULT_NODE);
//    }
    
    public Cfx getCfx() {
        return cfx;
    }
    
    public BigInteger getChainId() {
        return chainId;
    }
    
    public BigInteger getGasPrice() {
        return gasPrice;
    }
    
    @Override
    public String getName() {
        return "Conflux";
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }
    
    @Override
    public Icon getIcon(int size) {
        return ImageUtilities.loadImageIcon("/resource/cfx" + size + ".png", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/cfx" + size + ".png", true);
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
    public void setDefaultNode() {
        setNode(DEFAULT_NODE);
    }

    @Override
    public void setNode(String node) {
        try {
            if(cfx != null) {
                cfx.close();
            }
        } catch (IOException ex) {
            LOG.log(Level.WARNING, null, ex);
        }
        cfx = Cfx.create(node);
        // 获得chainId
        Status status = cfx.getStatus().sendAndGet();
        chainId = status.getChainId();
        RawTransaction.setDefaultChainId(chainId);
        
        // 获得gasPrice
        gasPrice = cfx.getGasPrice().sendAndGet();
    }
    

    @Override
    public void close() {
        try {
            if(cfx != null) {
                cfx.close();
            }
        } catch (IOException ex) {
            LOG.log(Level.WARNING, null, ex);
        }
    }
    
    /**
     * 
     * @param name
     * @param pwd
     * @param mnemonicWords
     * @return 
     */
    @Override
    public Pair<String, String> createWalletByMnemonic(List<String> mnemonicWords) {
        // 1 根据助记词生成私钥
        BigInteger pathPrivateKey = Bip44Utils.getPathPrivateKey(mnemonicWords, BIP44PATH);
        
        ECKeyPair ecKeyPair = ECKeyPair.create(pathPrivateKey);
//        String publicKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey());
        String privateKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPrivateKey());
        Account account = Account.create(cfx, privateKey);

        return new Pair<>(account.getAddress(), privateKey);
    }

//    @Override
//    public Wallet createWalletByMnemonic(String name, String pwd, List<String> mnemonicWords) {
//        // 1 根据助记词生成私钥
//        BigInteger pathPrivateKey = Bip44Utils.getPathPrivateKey(mnemonicWords, BIP44PATH);
//        
//        ECKeyPair ecKeyPair = ECKeyPair.create(pathPrivateKey);
////        String publicKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPublicKey());
//        String privateKey = Numeric.toHexStringWithPrefix(ecKeyPair.getPrivateKey());
//        Account account = Account.create(cfx, privateKey);
//
//        // 2 密码取md5并保存
//        String pwdMD5 = DigestUtils.md5DigestAsHex(pwd.getBytes()); 
//
//        // 3 助记词和私钥加密保存
//        String mnemonicAES = AESUtil.encrypt(StringUtils.join(mnemonicWords, " "), pwd);
//        String privateKeyAES = AESUtil.encrypt(privateKey, pwd);
//
//        return new Wallet(name, pwdMD5, SYMBOL, account.getAddress(), privateKeyAES, mnemonicAES, new Date());
//    }
    
    @Override
    public String importWalletByPrivateKey(String privateKey) {
        return Account.create(cfx, privateKey).getAddress();
    }

//    @Override
//    public Wallet importWalletByPrivateKey(String name, String pwd, String privateKey) {
//        Account account = Account.create(cfx, privateKey);
//        
//        // 2 密码取md5并保存
//        String pwdMD5 = DigestUtils.md5DigestAsHex(pwd.getBytes()); 
//
//        // 3 私钥加密保存
//        String privateKeyAES = AESUtil.encrypt(privateKey, pwd);
//
//        return new Wallet(name, pwdMD5, SYMBOL, account.getAddress(), privateKeyAES, new Date());
//    }
    
    @Override
    public Pair<String, String>  importWalletByMnemonic(String mnemonic) {
        return createWalletByMnemonic(Arrays.asList(StringUtils.split(mnemonic, " ")));
    }

//    @Override
//    public Wallet importWalletByMnemonic(String name, String pwd, String mnemonic) {
//        List<String> mnemonicWords = Arrays.asList(StringUtils.split(mnemonic, " "));
//        return createWalletByMnemonic(name, pwd, mnemonicWords);
//    }

    @Override
    public boolean isValidAddress(String address) {
        try {
            // 合约地址也可以转账
            Address.validate(address);
            return true;
        } catch(AddressException e) {
            // ignore
            return false;
        }
        
    }

    @Override
    public String getTransactionDetailUrl(String hash) {
        return TRANSACTIONS_DETAIL_URL + hash;
    }
}
