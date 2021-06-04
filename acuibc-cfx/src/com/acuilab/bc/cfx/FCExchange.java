package com.acuilab.bc.cfx;

import com.acuilab.bc.main.cfx.IFCExchange;
import conflux.web3j.Cfx;
import conflux.web3j.contract.ContractCall;
import conflux.web3j.contract.abi.TupleDecoder;
import conflux.web3j.types.Address;
import java.util.logging.Logger;
import org.openide.util.Lookup;

/**
 *
 * @author acuilab.com
 */
public class FCExchange implements IFCExchange {
    
    private static final Logger LOG = Logger.getLogger(CUSDCCoin.class.getName());

    public static final String CONTRACT_ADDRESS = "cfx:acdrd6ahf4fmdj6rgw4n9k4wdxrzfe6ex6jc7pw50m";

    @Override
    public UserInfo userInfos() {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        ContractCall contract = new ContractCall(cfx, new Address(CONTRACT_ADDRESS));
        // passing method name and parameter to `contract.call`
        // note: parameters should use web3j.abi.datatypes type
        String value = contract.call("userInfos", new Address("cfx:aapvvj1gt07k5d8vs18w2z1ymhkenfw2k2smvbz674").getABIAddress()).sendAndGet();
        TupleDecoder decoder = new TupleDecoder(value);
        System.out.println("0--------------------------------------" + decoder.nextUint256());
        System.out.println("1--------------------------------------" + decoder.nextUint256());
        System.out.println("2--------------------------------------" + decoder.nextUint256());
        System.out.println("3--------------------------------------" + decoder.nextBool());
        System.out.println("4--------------------------------------" + decoder.nextUint256());
        System.out.println("5--------------------------------------" + decoder.nextUint256());
//        System.out.println("value====================================="  + value);
//        List<Type> valueDecode = DecodeUtil.decode(value, new StaticArrayTypeReference<org.web3j.abi.datatypes.Type>() {});
//        
//	// 转成BigInteger数组
//        System.out.println("xxxxxxxxxxxxxxxxxxxxxxx=" + valueDecode.size());
//	org.web3j.abi.datatypes.Type[] ret = new org.web3j.abi.datatypes.Type[valueDecode.size()];
//	for(int i=0; i<valueDecode.size(); i++) {
//	    System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx=" + valueDecode.get(i).getValue());
//	}
        
//        System.out.println("0--------------------------------------" + list.get(0));
//        System.out.println("1--------------------------------------" + list.get(1));
//        System.out.println("2--------------------------------------" + list.get(2));
//        System.out.println("3--------------------------------------" + list.get(3));
//        System.out.println("4--------------------------------------" + list.get(4));
//        System.out.println("5--------------------------------------" + list.get(5));
//        com.acuilab.bc.cfx.util.FCExchange exchange = com.acuilab.bc.cfx.util.FCExchange.load(CONTRACT_ADDRESS, org.web3j.protocol.Web3j.build(new HttpService("https://mainnet-rpc.conflux-chain.org.cn/v2")), Credentials.create("xxx"), BigInteger.ONE, BigInteger.valueOf(214438));
//        try {
//            Tuple6<BigInteger, BigInteger, BigInteger, Boolean, BigInteger, BigInteger> tuple6 = exchange.userInfos("yyy").send();
//            
//            System.out.println("0--------------------------------------" + tuple6.component1());
//            System.out.println("1--------------------------------------" + tuple6.component2());
//            System.out.println("2--------------------------------------" + tuple6.component3());
//            System.out.println("3--------------------------------------" + tuple6.component4());
//            System.out.println("4--------------------------------------" + tuple6.component5());
//            System.out.println("5--------------------------------------" + tuple6.component6());
//        } catch (Exception ex) {
//            Logger.getLogger(FCExchange.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        
        
        return null;
    }

}
