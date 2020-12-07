package com.acuilab.bc.cfx;

import com.acuilab.bc.main.coin.ICFXCoin;
import com.acuilab.bc.main.wallet.TransferRecord;
import com.acuilab.bc.main.wallet.Wallet;
import com.google.common.collect.Lists;
import conflux.web3j.Account;
import conflux.web3j.Cfx;
import conflux.web3j.CfxUnit;
import conflux.web3j.types.RawTransaction;
import java.awt.Image;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.logging.Logger;
import javax.swing.Icon;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import com.acuilab.bc.main.coin.ICoin;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    
    public static final String STAKING_CONTRACT_ADDRESS = "0x0888000000000000000000000000000000000002";

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
        return CFXBlockChain.SYMBOL;
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
//    public Type getType() {
//        return Type.BASE;
//    }

    @Override
    public BigInteger balanceOf(String address) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        return cfx.getBalance(address).sendAndGet();
    }
    
    /**
     * 转账
     * @param to        接收地址
     * @param value     转账数量
     * @param gas       矿工费: 21000~100000000drip
     */
    @Override
    public String transfer(String privateKey, String to, BigInteger value, BigInteger gas) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        Account account = Account.create(cfx, privateKey);
        if(gas == null) {
            return account.transfer(to, value);
        }
        
        BigInteger currentEpoch = cfx.getEpochNumber().sendAndGet();
        return account.mustSend(RawTransaction.create(account.getNonce(), gas, to, value, BigInteger.ZERO, currentEpoch, null));
        
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
        if(limit > 100) {
            // "query.pageSize" do not match condition "<=100", got: 140
            limit = 100;
        }
        String url = TRANSACTION_LIST_URL + "?skip=0&reverse=true&limit=" + limit + "&accountAddress=" + address;
        System.out.println("url=" + url);
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
            for (final JsonNode objNode : list) {
                TransferRecord transferRecord = new TransferRecord();
                transferRecord.setWalletName(wallet.getName());
                transferRecord.setWalletAddress(wallet.getAddress());
                transferRecord.setCoinName(coin.getName());
                JsonNode value = objNode.get("value");
                transferRecord.setValue(coin.minUnit2MainUint(new BigInteger(value.asText("0"))).setScale(coin.getMainUnitScale(), RoundingMode.HALF_DOWN).stripTrailingZeros().toPlainString());
                JsonNode gasPrice = objNode.get("gasPrice");
                transferRecord.setGasPrice(gasPrice.asText());
                JsonNode gas = objNode.get("gas");
                transferRecord.setGas(gas.asText());
                JsonNode status = objNode.get("status");
                transferRecord.setStatus("" + status.asInt());
                JsonNode blockHash = objNode.get("blockHash");
                transferRecord.setBlockHash(blockHash.asText());
                JsonNode from = objNode.get("from");
                transferRecord.setSendAddress(from.asText());
                JsonNode to = objNode.get("to");
                transferRecord.setRecvAddress(to.asText());
                JsonNode hash = objNode.get("hash");
                transferRecord.setHash(hash.asText());
                JsonNode timestamp = objNode.get("timestamp");
                transferRecord.setTimestamp(new Date(timestamp.asLong()*1000));

                transferRecords.add(transferRecord);
            }
        }

        return transferRecords;
    }

    @Override
    public int gasMin(String address) {
        return CfxUnit.DEFAULT_GAS_LIMIT.intValue();
    }

    @Override
    public int gasMax(String address) {
        // @see http://acuilab.com:8080/articles/2020/08/12/1597238136717.html
        return (int)(CfxUnit.DEFAULT_GAS_LIMIT.intValue() * 1.3);  // 向下取整
    }

    @Override
    public int gasDefaultValue(String address) {
        return CfxUnit.DEFAULT_GAS_LIMIT.intValue();
    }

    @Override
    public String gasDesc(int gas) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        BigInteger gasValue = bc.getGasPrice().multiply(BigInteger.valueOf(gas));
        return gasValue + " drip/" + CfxUnit.drip2Cfx(gasValue).toPlainString() + " CFX";
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
        
        return account.call(STAKING_CONTRACT_ADDRESS, "deposit", new org.web3j.abi.datatypes.Uint(value));
    }

    @Override
    public String withdraw(String privateKey, BigInteger value) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        Account account = Account.create(cfx, privateKey);
        
        return account.call(STAKING_CONTRACT_ADDRESS, "withdraw", new org.web3j.abi.datatypes.Uint(value));
    }

    @Override
    public BigInteger stakingBalanceOf(String address) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        return cfx.getStakingBalance(address).sendAndGet();
    }
    
    @Override
    public BigInteger getDefaultGas() {
        return CfxUnit.DEFAULT_GAS_LIMIT.multiply(CfxUnit.DEFAULT_GAS_PRICE);
    }

}
