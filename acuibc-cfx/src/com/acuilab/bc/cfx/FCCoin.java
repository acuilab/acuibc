package com.acuilab.bc.cfx;

import com.acuilab.bc.main.wallet.Coin;
import com.acuilab.bc.main.wallet.TransferRecord;
import com.acuilab.bc.main.wallet.Wallet;
import com.google.common.collect.Lists;
import conflux.web3j.Account;
import conflux.web3j.Cfx;
import conflux.web3j.CfxUnit;
import conflux.web3j.Request;
import conflux.web3j.contract.ERC20Call;
import conflux.web3j.contract.ERC20Executor;
import conflux.web3j.request.LogFilter;
import conflux.web3j.response.Log;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.openide.util.Lookup;

/**
 *
 * @author admin
 */
public class FCCoin implements Coin {

    public static final String CONTRACT_ADDRESS = "0x87010faf5964d67ed070bc4b8dcafa1e1adc0997";

    public static final String NAME = "Fans Coin";
    public static final String SYMBOL = "FC";

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
    public String getMainUnit() {
        return "FC";
    }

    @Override
    public String getMinUnit() {
        return "";  // 没有最小单位
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

    @Override
    public int getMainUnitScale() {
        return 6;
    }

    @Override
    public int getScale() {
        return 18;
    }

    @Override
    public Type getType() {
        return Type.TOKEN;
    }

    @Override
    public BigInteger balanceOf(String address) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        ERC20Call call = new ERC20Call(cfx, CONTRACT_ADDRESS);
        return call.balanceOf(address);
    }

    @Override
    public String transfer(String privateKey, String to, BigInteger value, BigInteger gas) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        Account account = Account.create(cfx, privateKey);
        ERC20Executor exec = new ERC20Executor(account, CONTRACT_ADDRESS);
        // 忽略gas参数，让sdk自己估算吧
        return exec.transfer(new Account.Option(), to, value);
    }

    @Override
    public List<TransferRecord> getTransferRecords(Wallet wallet, Coin coin, String address, int limit) throws Exception {
//        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
//        Cfx cfx = bc.getCfx();
//        LogFilter filter = new LogFilter();
//        filter.setAddress(Arrays.asList(CONTRACT_ADDRESS));
//        filter.setLimit((long)limit);
//        Request<List<Log>, Log.Response> req = cfx.getLogs(filter);
//        List<Log> logs = req.sendAndGet();
//        System.out.println("logs.size=" + logs.size());
//
//        for (Log log : logs) {
//            System.out.println("address=" + log.getAddress());
//            System.out.println("data=" + log.getData());
//            List<String> topics = log.getTopics();
//            for (String topic : topics) {
//                System.out.println("topic=" + topic);
//            }
//            System.out.println("transactionHash=" + log.getTransactionHash());
//            System.out.println("————————————————————————————————————————————————————");
//        }
        
        return Lists.newArrayList();
    }

    @Override
    public int gasMin() {
        return CfxUnit.DEFAULT_GAS_LIMIT.intValue();
    }

    @Override
    public int gasMax() {
        // @see http://acuilab.com:8080/articles/2020/08/12/1597238136717.html
        return (int) (CfxUnit.DEFAULT_GAS_LIMIT.intValue() * 1.3);  // 向下取整
    }

    @Override
    public int gasDefaultValue() {
        return CfxUnit.DEFAULT_GAS_LIMIT.intValue();
    }

    @Override
    public String gasDesc(int gas) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        BigInteger gasValue = bc.getGasPrice().multiply(BigInteger.valueOf(gas));
        return gasValue + " drip/" + CfxUnit.drip2Cfx(gasValue).toPlainString() + " CFX";
    }
}
