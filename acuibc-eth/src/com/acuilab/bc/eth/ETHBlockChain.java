package com.acuilab.bc.eth;



import java.util.logging.Logger;
import com.acuilab.bc.main.BlockChain;
import java.awt.Image;
import java.util.List;
import javax.swing.Icon;
import org.javatuples.Pair;
import org.openide.util.ImageUtilities;

/**
 *
 * @author admin
 */
public class ETHBlockChain implements BlockChain {
    
    private static final Logger LOG = Logger.getLogger(ETHBlockChain.class.getName());
    
    public static final String DEFAULT_NODE = "http://mainnet-jsonrpc.conflux-chain.org:12537"; // 默认结点地址
    public static final String BIP44PATH = "m/44'/60'/0'/0/0";  // 通用的以太坊基于bip44协议的助记词路径
    public static final String SYMBOL = "ETH(暂不可用)";

    @Override
    public void setDefaultNode() {
    }

    @Override
    public void setNode(String node) {
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
    }

    @Override
    public String getName() {
        return "Ethereum";
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

//    @Override
//    public Wallet createWalletByMnemonic(String name, String pwd, List<String> mnemonicWords) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public Wallet importWalletByPrivateKey(String name, String pwd, String privateKey) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }
//
//    @Override
//    public Wallet importWalletByMnemonic(String name, String pwd, String mnemonic) {
//        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//    }

    @Override
    public boolean isValidAddress(String address) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getTransactionDetailUrl(String hash) {
        return null;
    }

    @Override
    public Pair<String, String> createWalletByMnemonic(List<String> mnemonicWords) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String importWalletByPrivateKey(String privateKey) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Pair<String, String> importWalletByMnemonic(String mnemonic) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public TransactionStatus getTransactionStatusByHash(String hash) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
