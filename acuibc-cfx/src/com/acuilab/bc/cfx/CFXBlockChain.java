package com.acuilab.bc.cfx;

import conflux.web3j.Account;
import conflux.web3j.Cfx;
import conflux.web3j.contract.ContractCall;
import java.io.IOException;
import java.math.BigInteger;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.web3j.crypto.ECKeyPair;
import org.web3j.utils.Numeric;
import party.loveit.bip44forjava.utils.Bip44Utils;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
import com.acuilab.bc.main.BlockChain;
import com.acuilab.bc.main.util.Constants;
import conflux.web3j.contract.abi.DecodeUtil;
import conflux.web3j.response.Status;
import conflux.web3j.response.Transaction;
import conflux.web3j.types.Address;
import conflux.web3j.types.AddressException;
import conflux.web3j.types.RawTransaction;
import java.awt.Image;
import java.util.Arrays;
import org.javatuples.Pair;
import org.web3j.ens.NameHash;


/**
 *
 * @author admin
 */
public class CFXBlockChain implements BlockChain {
    
    private static final Logger LOG = Logger.getLogger(CFXBlockChain.class.getName());
    
    public static final String DEFAULT_NODE = "http://wallet-main.confluxrpc.org"; // 默认结点地址
    public static final String BIP44PATH = "m/44'/503'/0'/0/0";
    
    public static final String TRANSACTIONS_DETAIL_URL = "http://www.confluxscan.io/transaction/";
    public static final int REFRESH_DELAY_MILLISECONDS = 2000;  // 延时毫秒数，以便服务器准备交易记录和余额
    public static final int GET_TRANSACTION_STATUS_INTERVAL_MILLISECONDS = 2000;  // 获得交易状态的时间间隔
    
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
        return Constants.CFX_BLOCKCHAIN_SYMBAL;
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
    
    /**
     * 通过私钥导入钱包
     * @param privateKey
     * @return 
     */
    @Override
    public String importWalletByPrivateKey(String privateKey) {
        return Account.create(cfx, privateKey).getAddress();
    }
    
    /**
     * 通过助记词导入钱包
     * @param mnemonic 空格分隔的助记词字符串
     * @return 返回地址和私钥对
     */
    @Override
    public Pair<String, String>  importWalletByMnemonic(String mnemonic) {
        return createWalletByMnemonic(Arrays.asList(StringUtils.split(mnemonic, " ")));
    }

    @Override
    public boolean isValidAddress(String address) {
        try {
            // 合约地址也可以转账
            Address.validate(address);
            return true;
        } catch(AddressException e) {
	    LOG.log(Level.WARNING, null, e);
        }
	
	return false;
    }
    @Override
    public String getAddressFromDomain(String cns) {
        try {
            //域名解析合约地址CNS Alan SKY, 之后应该改为由读取合约得到
            ContractCall contract = new ContractCall(cfx, "0x88fb20bd7e08d8d7333be177d584ca8779ae0a3a");
            String nameHash = NameHash.nameHash(cns);
            System.out.println("nameHash:"+nameHash);
            BigInteger tokenId = new BigInteger(StringUtils.substringAfter(nameHash, "0x"),16);

            System.out.println("tokenId:"+tokenId);
            String address = contract.call("get", new org.web3j.abi.datatypes.generated.Uint256(tokenId), new org.web3j.abi.datatypes.Utf8String("wallet.CFX.address")).sendAndGet();
            String addresDecode = DecodeUtil.decode(address, org.web3j.abi.datatypes.Utf8String.class);
          
            System.out.println("addresDecode:"+addresDecode);
            return addresDecode;
        } catch(AddressException e) {
	    LOG.log(Level.WARNING, null, e);
        }
	
	return null;
    }

    @Override
    public String getTransactionDetailUrl(String hash) {
        return TRANSACTIONS_DETAIL_URL + hash;
    }

    @Override
    public TransactionStatus getTransactionStatusByHash(String hash) {
        
        int count = 8;
        while(count > 0) {
            Transaction trans = cfx.getTransactionByHash(hash).sendAndGet().orElse(null);
            if(trans != null) {
                String blockHash =  trans.getBlockHash().orElse(null);
                if(blockHash != null) {
                    
                    BigInteger result = trans.getStatus().orElse(null);
                    
                    // '0x0'成功执行; '0x1'异常发生，但是nonce值增加; '0x2' 异常发生，并且nonce值没有增加.
                    if(result != null && result.equals(BigInteger.ZERO)) {
                        // 成功执行: 延时，以便服务器准备交易记录和余额
                        try {
                            Thread.sleep(REFRESH_DELAY_MILLISECONDS);
                        } catch (InterruptedException ex) {
                        }
                        
                        return TransactionStatus.SUCCESS;
                    }
                    
                    return TransactionStatus.FAILED;
                }
            }
            
            count--;
            
            // 休眠2秒钟
            try {
                Thread.sleep(GET_TRANSACTION_STATUS_INTERVAL_MILLISECONDS);
            } catch (InterruptedException ex) {
            }
        }

        
        return TransactionStatus.UNKNOWN;
    }
}
