package com.acuilab.bc.eth;

import com.acuilab.bc.eth.util.EthUnit;
import com.acuilab.bc.main.wallet.TransferRecord;
import com.acuilab.bc.main.wallet.Wallet;
import java.awt.Image;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
import com.acuilab.bc.main.coin.ICoin;
import com.acuilab.bc.main.util.Constants;
import com.google.common.collect.Lists;
import org.openide.util.Lookup;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;

/**
 *
 * @author admin
 */
public class ETHCoin implements ICoin {
    
    private static final Logger LOG = Logger.getLogger(ETHCoin.class.getName());
    
    public static final String NAME = "ETH";
    public static final String SYMBOL = "ETH";
    // http://scan-dev-service.conflux-chain.org:8885/api/transaction/list?pageSize=10&page=1&accountAddress=0x176c45928d7c26b0175dec8bf6051108563c62c5
    public static final String TRANSACTION_LIST_URL = "https://confluxscan.io/v1/transaction";
    
    @Override
    public void init() {
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getSymbol() {
        return SYMBOL;
    }
    
    @Override
    public String getBlockChainSymbol() {
        return Constants.ETH_BLOCKCHAIN_SYMBAL;
    }
    
    @Override
    public Icon getIcon(int size) {
        return ImageUtilities.loadImageIcon("/resource/eth" + size + ".png", true);
    }

    @Override
    public Image getIconImage(int size) {
        return ImageUtilities.loadImage("/resource/eth" + size + ".png", true);
    }

    @Override
    public BigInteger balanceOf(String address) throws Exception {
        ETHBlockChain bc = Lookup.getDefault().lookup(ETHBlockChain.class);
        Web3j web3 = bc.getWeb3j();
        return web3.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();
    }
    
    /**
     * 转账
     * @param to        接收地址
     * @param value     转账数量
     * @param gas       矿工费: 21000~100000000wei
     * @return 转账哈希
     */
    @Override
    public String transfer(String privateKey, String to, BigInteger value, BigInteger gas) throws Exception {
        ETHBlockChain bc = Lookup.getDefault().lookup(ETHBlockChain.class);
        Web3j web3 = bc.getWeb3j();
	
	return "";
    }

    @Override
    public String getMainUnit() {
        return "ETH";
    }

    @Override
    public int getMainUnitScale() {
        return 6;
    }

    @Override
    public int getScale() {
        return 18;
    }

    @Override
    public String getMinUnit() {
        return "wei";
    }

    @Override
    public BigDecimal minUnit2MainUint(BigInteger minUnitValue) {
        return EthUnit.drip2Cfx(minUnitValue);
    }

    @Override
    public BigInteger mainUint2MinUint(double mainUnitValue) {
        return EthUnit.cfx2Drip(mainUnitValue);
    }

    @Override
    public BigInteger mainUint2MinUint(long mainUnitValue) {
        return EthUnit.cfx2Drip(mainUnitValue);
    }

    /**
     * 使用OkHttp同步请求交易记录
     * @param address
     * @param limit
     * @return 
     */
    @Override
    public List<TransferRecord> getTransferRecords(Wallet wallet, ICoin coin, String address, int limit) throws Exception {
        List<TransferRecord> transferRecords = Lists.newArrayList();

        return transferRecords;
    }

    // 单位drip
    @Override
    public int gasMin() {
	// 1drip
        return 1;
    }

    // 单位drip
    @Override
    public int gasMax() {
        // 100drip
        return 100;
    }

    // 单位drip
    @Override
    public int gasDefault() {
	// 1drip
        return 1;
    }
    
    @Override
    public int gasLimit() {
	return 25200;
    }

    @Override
    public String gasDesc(int gas) {
        return gas + "wei";
    }

    @Override
    public boolean isDivisible() {
        return true;
    }
}
