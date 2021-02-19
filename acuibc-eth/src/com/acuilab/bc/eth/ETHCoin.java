package com.acuilab.bc.eth;

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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import java.math.RoundingMode;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.apache.commons.lang3.math.NumberUtils;
import org.openide.util.Lookup;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

/**
 *
 * @author admin
 */
public class ETHCoin implements ICoin {

    private static final Logger LOG = Logger.getLogger(ETHCoin.class.getName());

    public static final String NAME = "ETH";
    public static final String SYMBOL = "ETH";
    // https://api-cn.etherscan.com/api?module=account&action=txlist&address=0xddbd2b932c763ba5b1b7ae3b362eac3e8d40121a&startblock=0&endblock=99999999&page=1&offset=10&sort=asc&apikey=YourApiKeyToken
    public static final String TRANSACTION_LIST_URL = "https://api-cn.etherscan.com/api";

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
        Admin admin = bc.getAdmin();
        return admin.ethGetBalance(address, DefaultBlockParameterName.LATEST).send().getBalance();
    }

    /**
     * 转账
     *
     * @param to 接收地址
     * @param value 转账数量
     * @param gas price，单位wei, 调用方负责转换gas2MinUnit
     * @return 转账哈希
     */
    @Override
    public String transfer(String privateKey, String to, BigInteger value, BigInteger gas) throws Exception {
        ETHBlockChain bc = Lookup.getDefault().lookup(ETHBlockChain.class);
        Admin admin = bc.getAdmin();

        Credentials credentials = Credentials.create(privateKey);

        EthGetTransactionCount ethGetTransactionCount = admin.ethGetTransactionCount(credentials.getAddress(), DefaultBlockParameterName.LATEST).sendAsync().get();

        BigInteger nonce = ethGetTransactionCount.getTransactionCount();
        
//        BigInteger nonce,
//        BigInteger gasPrice,
//        BigInteger gasLimit,
//        String to,
//        BigInteger value
        RawTransaction rawTransaction = RawTransaction.createEtherTransaction(
                nonce, 
                gas,
                BigInteger.valueOf(gasLimit()), 
                to, 
                value);
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        String hexValue = Numeric.toHexString(signedMessage);

        EthSendTransaction ethSendTransaction = admin.ethSendRawTransaction(hexValue).sendAsync().get();
        if (ethSendTransaction.hasError()) {
            throw new Exception(ethSendTransaction.getError().getMessage());
        }
        
        return ethSendTransaction.getTransactionHash();
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
        return Convert.fromWei(new BigDecimal(minUnitValue), Convert.Unit.ETHER);
    }

    @Override
    public BigInteger mainUint2MinUint(double mainUnitValue) {
        return Convert.toWei(new BigDecimal(mainUnitValue), Convert.Unit.ETHER).toBigInteger(); // .toBigIntegerExact()
    }

    @Override
    public BigInteger mainUint2MinUint(long mainUnitValue) {
        return Convert.toWei(new BigDecimal(mainUnitValue), Convert.Unit.ETHER).toBigInteger(); // .toBigIntegerExact()
    }
    
    /**
     * 将gas转换为最小单位（以太坊的gas单位太贵，一般以gwei为单位）
     * @param gas
     * @return 
     */
    @Override
    public BigInteger gas2MinUnit(long gas) {
	return Convert.toWei(new BigDecimal(gas), Convert.Unit.GWEI).toBigIntegerExact();
    }

    /**
     * 使用OkHttp同步请求交易记录
     *
     * @param address
     * @param limit
     * @return
     */
    @Override
    public List<TransferRecord> getTransferRecords(Wallet wallet, ICoin coin, String address, int limit) throws Exception {
        List<TransferRecord> transferRecords = Lists.newArrayList();
	
        if(limit > 50) {
            // "query.pageSize" do not match condition "<=100", got: 140
            limit = 50;
        }
        String url = TRANSACTION_LIST_URL + "?module=account&action=txlist&startblock=0&endblock=99999999&sort=desc&apikey=EH2VSUF39PWJG4RP36W8DIV741SUID1JHX&page=1&offset=" + limit + "" + "&address=" + address;
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
            
            JsonNode list = root.get("result");
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
                JsonNode gas = objNode.get("gas");
                transferRecord.setGas(gas.asText());
                JsonNode status = objNode.get("isError");
                transferRecord.setStatus("" + status.asInt());
                JsonNode blockHash = objNode.get("blockHash");
                transferRecord.setBlockHash(blockHash.asText());
                JsonNode from = objNode.get("from");
                transferRecord.setSendAddress(from.asText());
                JsonNode to = objNode.get("to");
                transferRecord.setRecvAddress(to.asText());
                JsonNode hash = objNode.get("hash");
                transferRecord.setHash(hash.asText());
                JsonNode timestamp = objNode.get("timeStamp");
                transferRecord.setTimestamp(new Date(NumberUtils.toLong(timestamp.asText())*1000));

                transferRecords.add(transferRecord);
                }
            }
            
        }

//        EtherScanApi api = new EtherScanApi("EH2VSUF39PWJG4RP36W8DIV741SUID1JHX", EthNetwork.KOVAN);
//        
//        List<Tx> txList = api.account().txs(address);
//        for(Tx tx : txList) {
//            TransferRecord transferRecord = new TransferRecord();
//            transferRecord.setWalletName(wallet.getName());
//            transferRecord.setWalletAddress(wallet.getAddress());
//            transferRecord.setCoinName(coin.getName());
//            transferRecord.setValue(coin.minUnit2MainUint(tx.getValue()).setScale(coin.getMainUnitScale(), RoundingMode.HALF_DOWN).stripTrailingZeros().toPlainString());
//            transferRecord.setGasPrice(tx.getGasPrice().toString());
//            transferRecord.setGas(tx.getGas().toString());
//            // 交易状态(0 代表成功，1 代表发生错误，当交易被跳过或未打包时为null)
//            transferRecord.setStatus(tx.haveError() ? "1" : "0");
//            transferRecord.setBlockHash(tx.getBlockHash());
//            transferRecord.setSendAddress(tx.getFrom());
//            transferRecord.setRecvAddress(tx.getTo());
//            transferRecord.setHash(tx.getHash());
//            // 获取 JVM 启动时获取的时区
//            Instant instant = tx.getTimeStamp().atZone(TimeZone.getDefault().toZoneId()).toInstant();  // 时区
//            transferRecord.setTimestamp(Date.from(instant));
//
//            transferRecords.add(transferRecord);
//        }
        
        return transferRecords;
    }

    // 单位gwei
    @Override
    public int gasMin() {
        // gwei
        return 100;
    }

    // 单位gwei
    @Override
    public int gasMax() {
        // gwei
        return 300;
    }

    // 单位gwei
    @Override
    public int gasDefault() {
        // gwei
        return 121;
    }

    @Override
    public int gasLimit() {
        return 45000;
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
