package com.acuilab.bc.cfx;

import com.acuilab.bc.main.cfx.CFXExtend;
import conflux.web3j.Account;
import conflux.web3j.Cfx;
import conflux.web3j.types.Address;
import conflux.web3j.types.RawTransaction;
import conflux.web3j.types.SendTransactionResult;
import conflux.web3j.types.TransactionBuilder;
import java.math.BigInteger;
import org.openide.util.Lookup;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Sign;
import org.web3j.crypto.StructuredDataEncoder;
import org.web3j.utils.Numeric;

/**
 *
 * @author acuilab.com
 */
public class CFXExtendImpl implements CFXExtend {

    @Override
    public String send(String privateKey, String from, BigInteger gas, String to, BigInteger value, BigInteger storageLimit, String data) throws Exception {
        CFXBlockChain bc = Lookup.getDefault().lookup(CFXBlockChain.class);
        Cfx cfx = bc.getCfx();
        
        Account account = Account.create(cfx, privateKey);
	
	TransactionBuilder txBuilder = new TransactionBuilder(new Address(from));
	txBuilder.withTo(new Address(to));
	if(gas != null) {
	    txBuilder.withGasPrice(gas);
	}
	if(value != null) {
	    txBuilder.withValue(value);
	}

	if(storageLimit != null) {
	    txBuilder.withStorageLimit(storageLimit);
	}
	txBuilder.withData(data);
	RawTransaction rawTx = txBuilder.build(cfx);
	SendTransactionResult result = account.send(rawTx);
	return result.getTxHash();
    }

    @Override
    public String sign(String privateKey, String data) throws Exception {
	System.out.println("data===" + data);
//	Credentials credentials = Credentials.create(privateKey);
//	
//	RlpType rlpType = RlpString.create(Numeric.hexStringToByteArray(data));
//	
//	byte[] encoded = RlpEncoder.encode(rlpType);
//	Sign.SignatureData signature = Sign.signMessage(encoded, credentials.getEcKeyPair());
//	
//	int v = signature.getV()[0] - 27;
//	byte[] r = Bytes.trimLeadingZeroes(signature.getR());
//	byte[] s = Bytes.trimLeadingZeroes(signature.getS());
//
//	byte[] signedData = RlpEncoder.encode(new RlpList(
//			rlpType,
//			RlpString.create(v),
//			RlpString.create(r),
//			RlpString.create(s)));
//	
//	return Numeric.toHexString(signedData);

//        StructuredDataEncoder dataEncoder = new StructuredDataEncoder(data);
        
//        byte[] encodedData =
//                        dataEncoder.encodeData(
//                                dataEncoder.jsonMessageObject.getPrimaryType(),
//                                (HashMap<String, Object>) dataEncoder.jsonMessageObject.getMessage());
//        
//        
//        dataEncoder.hashStructuredData();


//            dataEncoder.getStructuredData();

//        String data2 = "{\n" +
//"	\"types\": {\n" +
//"		\"EIP712Domain\": [{\n" +
//"			\"name\": \"name\",\n" +
//"			\"type\": \"string\"\n" +
//"		}, {\n" +
//"			\"name\": \"version\",\n" +
//"			\"type\": \"string\"\n" +
//"		}, {\n" +
//"			\"name\": \"chainId\",\n" +
//"			\"type\": \"uint256\"\n" +
//"		}, {\n" +
//"			\"name\": \"verifyingContract\",\n" +
//"			\"type\": \"address\"\n" +
//"		}],\n" +
//"		\"WithdrawRequest\": [{\n" +
//"			\"name\": \"userAddress\",\n" +
//"			\"type\": \"address\"\n" +
//"		}, {\n" +
//"			\"name\": \"amount\",\n" +
//"			\"type\": \"uint256\"\n" +
//"		}, {\n" +
//"			\"name\": \"recipient\",\n" +
//"			\"type\": \"address\"\n" +
//"		}, {\n" +
//"			\"name\": \"burn\",\n" +
//"			\"type\": \"bool\"\n" +
//"		}, {\n" +
//"			\"name\": \"nonce\",\n" +
//"			\"type\": \"uint256\"\n" +
//"		}]\n" +
//"	},\n" +
//"	\"primaryType\": \"WithdrawRequest\",\n" +
//"	\"domain\": {\n" +
//"		\"name\": \"CRCL\",\n" +
//"		\"version\": \"1.0\",\n" +
//"		\"chainId\": 1029,\n" +
//"		\"verifyingContract\": \"0x81893be75644b106c4b392b621dad15581748177\"\n" +
//"	},\n" +
//"	\"message\": {\n" +
//"		\"userAddress\": \"cfx:aapvvj1gt07k5d8vs18w2z1ymhkenfw2k2smvbz674\",\n" +
//"		\"amount\": \"1000000000000000000\",\n" +
//"		\"recipient\": \"cfx:aapvvj1gt07k5d8vs18w2z1ymhkenfw2k2smvbz674\",\n" +
//"		\"burn\": true,\n" +
//"		\"nonce\": 1618794925555\n" +
//"	}\n" +
//"}";
//        StructuredDataEncoder dataEncoder2 = new StructuredDataEncoder(data);
//        
//        
//        
//        System.out.println("test2222222222222=========================" + Numeric.toHexString(dataEncoder2.hashStructuredData()));
//            
//            
//
////        return Numeric.toHexString(dataEncoder.hashStructuredData());
//return Numeric.toHexString(dataEncoder2.hashStructuredData());


        StructuredDataEncoder sde = new StructuredDataEncoder(data);
        byte[] hash = sde.hashStructuredData();
        
        Credentials credentials = Credentials.create(privateKey);
        ECKeyPair ecKeyPair = credentials.getEcKeyPair();
        
        Sign.SignatureData sigData = Sign.signMessage(hash, ecKeyPair, false);
        
        byte[] rsv = new byte[sigData.getR().length + sigData.getS().length + sigData.getV().length];
        System.arraycopy(sigData.getR(), 0, rsv, 0, sigData.getR().length);
        System.arraycopy(sigData.getS(), 0, rsv, sigData.getR().length, sigData.getS().length);
        System.arraycopy(sigData.getV(), 0, rsv, sigData.getR().length + sigData.getS().length, sigData.getV().length);

        return Numeric.toHexString(rsv);
    }

}
