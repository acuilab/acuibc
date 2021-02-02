package com.acuilab.bc.cfx;

import com.acuilab.bc.main.wallet.TransferRecord;
import com.acuilab.bc.main.wallet.Wallet;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import conflux.web3j.Account;
import conflux.web3j.Cfx;
import conflux.web3j.contract.ERC20Call;
import conflux.web3j.contract.ERC20Executor;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import org.openide.util.Lookup;
import com.acuilab.bc.main.coin.ICoin;

/**
 *
 * @author admin
 */
public abstract class ERC20Coin implements ICoin {
    
    private static final Logger LOG = Logger.getLogger(ERC20Coin.class.getName());

    // http://scan-dev-service.conflux-chain.org:8885/api/transfer/list?pageSize=10&page=1&address=0x87010faf5964d67ed070bc4b8dcafa1e1adc0997&accountAddress=0x1eff4db4696253106ae18ca96e092a0f354ef7c8
    public static final String TRANSFER_LIST_URL = "https://confluxscan.io/v1/transfer";
    
    @Override
    public void init() {
    }    
    
    @Override
    public BigInteger balanceOf(String address) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        ERC20Call call = new ERC20Call(cfx, getContractAddress());
        return call.balanceOf(address);
    }

    @Override
    public String transfer(String privateKey, String to, BigInteger value, BigInteger gas) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        Account account = Account.create(cfx, privateKey);
        ERC20Executor exec = new ERC20Executor(account, getContractAddress());
        return exec.transfer(new Account.Option().withGasPrice(gas).withGasLimit(this.gasLimit()), to, value);
    }

    @Override
    public List<TransferRecord> getTransferRecords(Wallet wallet, ICoin coin, String address, int limit) throws Exception {
        List<TransferRecord> transferRecords = Lists.newArrayList();
        if(limit > 100) {
            // "query.pageSize" do not match condition "<=100", got: 140
            limit = 100;
        }
	// transferType: {ERC20,ERC721,ERC777,ERC1155}
        String url = TRANSFER_LIST_URL + "?skip=0&reverse=true&limit=" + limit + "&transferType=ERC20&address=" + getContractAddress() + "&accountAddress=" + address;

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
                JsonNode from = objNode.get("from");
                transferRecord.setSendAddress(from.asText());
                JsonNode to = objNode.get("to");
                transferRecord.setRecvAddress(to.asText());
                JsonNode hash = objNode.get("transactionHash");
                transferRecord.setHash(hash.asText());
                JsonNode timestamp = objNode.get("timestamp");
                transferRecord.setTimestamp(new Date(timestamp.asLong()*1000));

                transferRecords.add(transferRecord);
            }
        }

        return transferRecords;
    }

    @Override
    public int gasMin() {
	// 1drip
        return 1;
    }

    @Override
    public int gasMax() {
        // 100drip
        return 100;
    }

    @Override
    public int gasDefault() {
	// 1drip
        return 1;
    }
    
    @Override
    public int gasLimit() {
	return 100000;
    }

    @Override
    public String gasDesc(int gas) {
        return gas + "drip";
    }

}
