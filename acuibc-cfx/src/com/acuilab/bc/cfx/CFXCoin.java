package com.acuilab.bc.cfx;

import com.acuilab.bc.main.coin.Coin;
import com.acuilab.bc.main.wallet.TransferRecord;
import com.google.common.collect.Lists;
import conflux.web3j.Account;
import conflux.web3j.Cfx;
import conflux.web3j.Request;
import conflux.web3j.request.Epoch;
import conflux.web3j.request.LogFilter;
import conflux.web3j.response.BigIntResponse;
import conflux.web3j.response.Block;
import conflux.web3j.response.Log;
import conflux.web3j.response.Transaction;
import conflux.web3j.types.RawTransaction;
import java.math.BigInteger;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.openide.util.Lookup;

/**
 *
 * @author admin
 */
public class CFXCoin implements Coin {

    @Override
    public String getName() {
        return "CFX";
    }

    @Override
    public String getSymbol() {
        return "CFX";
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

    @Override
    public List<TransferRecord> transferRecord(String address) {
        List<TransferRecord> list = Lists.newArrayList();
        
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
//        LogFilter filter = new LogFilter();
//        System.out.println("address=" + address);
////        filter.setAddress(Arrays.asList(address));
//        filter.setLimit(20000l);
//        Request<List<Log>, Log.Response> req = cfx.getLogs(filter);
//        List<Log> logs = req.sendAndGet(3, 100000);
//        System.out.println("logs.size=" + logs.size());
//        for(Log log : logs) {
//            String address1 = log.getAddress();
//            String data = log.getData();
//            if(StringUtils.startsWith(data, "0x1")) {
//                System.out.println("address=" + log.getAddress());
//                System.out.println("data=" + log.getData());
//                List<String> topics = log.getTopics();
//                for(String topic : topics) {
//                    System.out.println("topic=" + topic);
//                }
//                System.out.println("transactionHash=" + log.getTransactionHash());
//                System.out.println("————————————————————————————————————————————————————");
//            }
//        }
        Request<BigInteger, BigIntResponse>  req = cfx.getEpochNumber(Epoch.latestState());
        BigInteger end = req.sendAndGet();
        getTransactionsByAddr(address, end.longValue() - 1000, end.longValue());
        
        return list;
    }
    
    private void getTransactionsByAddr(String address, long startBlockNumber, long endBlockNumber) {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        for(long i=startBlockNumber; i<=endBlockNumber; i++) {
            System.out.println("i========================================" + i);
            Request<Optional<Block>, Block.Response> req = cfx.getBlockByEpoch(Epoch.numberOf(i));
            Optional<Block> block = req.sendAndGet(3, 100000);
            List<Transaction> transactions = block.get().getTransactions();
            for(Transaction trans : transactions) {
                System.out.println(trans.getFrom());
                if(StringUtils.equalsAnyIgnoreCase(address, trans.getFrom(), trans.getTo().get())) {
                    System.out.println("tx hash=" + trans.getHash());
                    System.out.println("nonce =" + trans.getNonce());
                    System.out.println("blockHash =" + trans.getBlockHash());
                    System.out.println("transactionIndex=" + trans.getTransactionIndex().get());
                    System.out.println("from  =" + trans.getFrom());
                    System.out.println("to  =" + trans.getTo().get());
                    System.out.println("value   =" + trans.getValue());
                    System.out.println("time   =" + trans.getContractCreated().get());
                    System.out.println("gasPrice   =" + trans.getGasPrice());
                    System.out.println("gas   =" + trans.getGas());
                    System.out.println("epoch height   =" + trans.getEpochHeight());
                    System.out.println("————————————————————————————————————————————————————");
                }
                
            }
        }
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
    
}
