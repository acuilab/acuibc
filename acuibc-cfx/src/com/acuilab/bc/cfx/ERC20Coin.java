package com.acuilab.bc.cfx;

import static com.acuilab.bc.cfx.FCCoin.CONTRACT_ADDRESS;
import com.acuilab.bc.main.wallet.Coin;
import com.acuilab.bc.main.wallet.TransferRecord;
import com.acuilab.bc.main.wallet.Wallet;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import conflux.web3j.Account;
import conflux.web3j.Cfx;
import conflux.web3j.CfxUnit;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.contract.ERC20Call;
import conflux.web3j.contract.ERC20Executor;
import conflux.web3j.response.UsedGasAndCollateral;
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
import org.web3j.abi.datatypes.Address;

/**
 *
 * @author admin
 */
public abstract class ERC20Coin implements Coin {
    
    private static final Logger LOG = Logger.getLogger(ERC20Coin.class.getName());

    // http://scan-dev-service.conflux-chain.org:8885/api/transfer/list?pageSize=10&page=1&address=0x87010faf5964d67ed070bc4b8dcafa1e1adc0997&accountAddress=0x1eff4db4696253106ae18ca96e092a0f354ef7c8
    public static final String TRANSFER_LIST_URL = "http://scan-dev-service.conflux-chain.org:8885/v1/transfer";
    
    private BigInteger estimateGas;
    
    @Override
    public void init() {
    }    
    
//    @Override
//    public Type getType() {
//        return Type.TOKEN;
//    }

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
        // 忽略gas参数，让sdk自己估算吧
        
        if(gas == null) {
            return exec.transfer(new Account.Option(), to, value);
        }
        
        return exec.transfer(new Account.Option().withGasLimit(gas), to, value);
    }

    @Override
    public List<TransferRecord> getTransferRecords(Wallet wallet, Coin coin, String address, int limit) throws Exception {
        List<TransferRecord> transferRecords = Lists.newArrayList();
        if(limit > 100) {
            // "query.pageSize" do not match condition "<=100", got: 140
            limit = 100;
        }
        String url = TRANSFER_LIST_URL + "?skip=0&reverse=true&limit=" + limit + "&address=" + getContractAddress() + "&accountAddress=" + address;
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
    public int gasMin(String address) {
        if(estimateGas == null) {
            CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);

            ContractCall call = new ContractCall(bc.getCfx(), CONTRACT_ADDRESS);
            UsedGasAndCollateral usedGas = call.estimateGasAndCollateral("balanceOf", new Address(address)).sendAndGet();
            estimateGas = usedGas.getGasUsed();
        }

        return estimateGas.intValue();
    }

    @Override
    public int gasMax(String address) {
        // @see http://acuilab.com:8080/articles/2020/08/12/1597238136717.html
        return (int)(gasMin(address) * 1.3);
    }

    @Override
    public int gasDefaultValue(String address) {
        // 为避免多次请求，调用方直接取gasMin
        return gasMin(address);
    }

    @Override
    public String gasDesc(int gas) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        BigInteger gasValue = bc.getGasPrice().multiply(BigInteger.valueOf(gas));
        return gasValue + " drip/" + CfxUnit.drip2Cfx(gasValue).toPlainString() + " CFX";
    }

}
