package com.acuilab.bc.cfx;

import com.acuilab.bc.main.coin.ICFXCoin;
import com.acuilab.bc.main.wallet.TransferRecord;
import com.acuilab.bc.main.wallet.Wallet;
import com.google.common.collect.Lists;
import conflux.web3j.Account;
import conflux.web3j.Cfx;
import conflux.web3j.CfxUnit;
import java.awt.Image;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import com.acuilab.bc.main.coin.ICoin;
import com.acuilab.bc.main.util.Constants;
import com.acuilab.bc.main.util.GasRelated;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import conflux.web3j.Account.Option;
import conflux.web3j.types.Address;
import java.math.RoundingMode;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;

/**
 *
 * @author admin
 */
public class CFXCoin implements ICFXCoin {
    
    private static final Logger LOG = Logger.getLogger(CFXCoin.class.getName());
    
    public static final String NAME = "CFX";
    public static final String SYMBOL = "CFX";
    // http://scan-dev-service.conflux-chain.org:8885/api/transaction/list?pageSize=10&page=1&accountAddress=0x176c45928d7c26b0175dec8bf6051108563c62c5
    public static final String TRANSACTION_LIST_URL = "https://confluxscan.io/v1/transaction";
    
    public static final String STAKING_CONTRACT_ADDRESS = "cfx:aaejuaaaaaaaaaaaaaaaaaaaaaaaaaaaajrwuc9jnb";

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
    public BigInteger balanceOf(String address) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        return cfx.getBalance(new Address(address)).sendAndGet();
    }
    
    /**
     * 转账
     * @param to        接收地址
     * @param value     转账数量
     * @param gas       矿工费: 21000~100000000drip
     * @return 转账哈希
     */
    @Override
    public String transfer(String privateKey, String to, BigInteger value, BigInteger gas) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        Account account = Account.create(cfx, privateKey);
	return account.transfer(new Option().withGasPrice(gas).withGasLimit(gasLimit()), new Address(to), value);
    }

    @Override
    public String getMainUnit() {
        return "CFX";
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
        return "drip";
    }

    @Override
    public BigDecimal minUnit2MainUint(BigInteger minUnitValue) {
        return CfxUnit.drip2Cfx(minUnitValue);
    }

    @Override
    public BigInteger mainUint2MinUint(double mainUnitValue) {
        return CfxUnit.cfx2Drip(mainUnitValue);
    }

    @Override
    public BigInteger mainUint2MinUint(long mainUnitValue) {
        return CfxUnit.cfx2Drip(mainUnitValue);
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
        
        int skip = 0;
        int count = 0;
        
        while(count < limit) {
            int pageSize = limit - count < 100 ? limit - count : 100;
            List<TransferRecord> list = getTransferRecords(wallet, coin, address, skip, pageSize);
            if(list.isEmpty()) {
                break;
            }
            
            transferRecords.addAll(list);
            skip += 100;
            count += list.size();
        }

        return transferRecords;
    }
    
    private List<TransferRecord> getTransferRecords(Wallet wallet, ICoin coin, String address, int skip, int pageSize) throws Exception {
        List<TransferRecord> transferRecords = Lists.newArrayList();
        if(skip >= 10000) {
            return transferRecords;
        }
        
        // {"code":10001,"message":"skip(9999) + limit(2) > listLimit(10000) while exist any of [accountAddress,minTimestamp,maxTimestamp,minEpochNumber,maxEpochNumber]"}
        if(skip + pageSize > 10000) {
            pageSize = 10000 - skip;
        }
        
        String url = TRANSACTION_LIST_URL + "?skip=" + skip + "&reverse=true&limit=" + pageSize + "&accountAddress=" + address;
        System.out.println(url);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .build();
        final Call call = okHttpClient.newCall(request);
        okhttp3.Response response = call.execute();             // java.net.SocketTimeoutException
        ResponseBody body = response.body();
        if(body != null) {
            // 解析json
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(body.string());
            
            JsonNode list = root.get("list");
            if(list != null){
                for (final JsonNode objNode : list) {
                
                TransferRecord transferRecord = new TransferRecord();
                transferRecord.setWalletName(wallet.getName());
                transferRecord.setWalletAddress(wallet.getAddress());
                transferRecord.setCoinName(coin.getName());
                JsonNode value = objNode.get("value");
                transferRecord.setValue(coin.minUnit2MainUint(new BigInteger(value.asText("0"))).setScale(coin.getMainUnitScale(), RoundingMode.HALF_DOWN).stripTrailingZeros().toPlainString());
                JsonNode gasPrice = objNode.get("gasPrice");
                transferRecord.setGasPrice(gasPrice.asText());
                JsonNode gas = objNode.get("gasFee");
                transferRecord.setGas(gas.asText());
                JsonNode status = objNode.get("status");
                transferRecord.setStatus("" + status.asInt());
                JsonNode blockHash = objNode.get("blockHash");
                transferRecord.setBlockHash(blockHash.asText());
                JsonNode from = objNode.get("from");
                transferRecord.setSendAddress(new Address(from.asText()).getAddress());
                JsonNode to = objNode.get("to");
                transferRecord.setRecvAddress(new Address(to.asText()).getAddress());
                JsonNode hash = objNode.get("hash");
                transferRecord.setHash(hash.asText());
                JsonNode timestamp = objNode.get("timestamp");
                transferRecord.setTimestamp(new Date(timestamp.asLong()*1000));

                transferRecords.add(transferRecord);
                }
            }
            
        }
        
        return transferRecords;
    }

//    // 单位drip
//    @Override
//    public int gasMin() {
//	// 1drip
//        return 1;
//    }
//
//    // 单位drip
//    @Override
//    public int gasMax() {
//        // 100drip
//        return 100;
//    }
//
//    // 单位drip
//    @Override
//    public int gasDefault() {
//	// 1drip
//        return CfxUnit.DEFAULT_GAS_PRICE.intValue();
//    }
    
    @Override
    public GasRelated getGasRelated() throws Exception {
        return new GasRelated(1000, 100000,60000);
    }
    
    @Override
    public int gasLimit() {
	return 25200;
    }

    @Override
    public String gasDesc(int gas) {
        return gas + "drip";
    }

    @Override
    public boolean isDivisible() {
        return true;
    }

    @Override
    public String deposit(String privateKey, BigInteger value) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        Account account = Account.create(cfx, privateKey);
        return account.call(new Address(STAKING_CONTRACT_ADDRESS), "deposit", new org.web3j.abi.datatypes.Uint(value));
    }

    @Override
    public String withdraw(String privateKey, BigInteger value) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        Account account = Account.create(cfx, privateKey);
        return account.call(new Address(STAKING_CONTRACT_ADDRESS), "withdraw", new org.web3j.abi.datatypes.Uint(value));
    }

    @Override
    public BigInteger stakingBalanceOf(String address) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        return cfx.getStakingBalance(new Address(address)).sendAndGet();
    }

    /**
     * 批量转账
     * @param privateKey    私钥
     * @param to        接收地址列表
     * @param value     转账数量列表
     * @param gas       矿工费
     * @throws java.lang.Exception
     */
    @Override
    public void batchTransfer(String privateKey, String[] tos, BigInteger[] values, BigInteger gas, BatchTransferCallback callback) throws Exception {
	
	if(tos.length != values.length) {
	    throw new java.lang.IllegalArgumentException("tos.length must equal with values.length");
	}
	
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        Account account = Account.create(cfx, privateKey);
	BigInteger nonce = account.getNonce();
	
	for(int i=0; i<tos.length; i++) {
	    String to = tos[i];
	    BigInteger value = values[i];
            
            // 过滤掉非法地址
            if(bc.isValidAddress(to)) {
                account.setNonce(nonce);
                String hash = account.transfer(new Option().withGasPrice(gas).withGasLimit(gasLimit()), new Address(to), value);
                callback.transferFinished(to, hash, i, tos.length);

                nonce = nonce.add(BigInteger.ONE);
            }
	}
    }
}
