package com.acuilab.bc.cfx;

import com.acuilab.bc.main.wallet.Coin;
import com.acuilab.bc.main.wallet.TransferRecord;
import com.google.common.collect.Lists;
import conflux.web3j.Account;
import conflux.web3j.Cfx;
import conflux.web3j.CfxUnit;
import conflux.web3j.Request;
import conflux.web3j.response.BigIntResponse;
import conflux.web3j.types.RawTransaction;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.openide.util.Lookup;

/**
 *
 * @author admin
 */
public class CFXCoin implements Coin {
    
    public static final String NAME = "CFX";
    public static final String SYMBOL = "CFX";
    // http://47.102.164.229:8885/api/transaction/list?page=1&pageSize=10&txType=all&accountAddress=0x18549f607b2023585d04cbb72eb7d05b4c980355
    public static final String TRANSACTION_LIST_URL = "http://47.102.164.229:8885/api/transaction/list";

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
    public Type getType() {
        return Type.BASE;
    }

    @Override
    public BigInteger balanceOf(String address) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        Request<BigInteger, BigIntResponse> req = cfx.getBalance(address);
        
        return req.sendAndGet();
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
        BigInteger currentEpoch = cfx.getEpochNumber().sendAndGet();
        
        RawTransaction.setDefaultChainId(bc.getChainId());
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
    public List<TransferRecord> getTransferRecords(String address, int limit) throws Exception {
        String url = TRANSACTION_LIST_URL + "?page=1&pageSize=" + limit + "&txType=all&accountAddress=" + address;
        OkHttpClient okHttpClient = new OkHttpClient();
        final okhttp3.Request request = new okhttp3.Request.Builder()
                .url(url)
                .build();
        final Call call = okHttpClient.newCall(request);
        okhttp3.Response response = call.execute();
        System.out.println(response.body().string());

        return Lists.newArrayList();
    }

}
