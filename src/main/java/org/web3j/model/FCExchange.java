package org.web3j.model;

import io.reactivex.Flowable;
import io.reactivex.functions.Function;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple6;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 4.8.4.
 */
@SuppressWarnings("rawtypes")
public class FCExchange extends Contract {
    public static final String BINARY = "60806040523480156200001157600080fd5b50604051620015da380380620015da833981810160405260408110156200003757600080fd5b50805160209091015160058054600680546001600160a01b0319166001600160a01b03808616919091179091556000600381905560048181556001829055610100600160a81b0319909316610100928716929092029190911760ff1916909255604080516329965a1d60e01b8152309281018390527fb281fc8c12954d22544db45de3159a39272895b169a852b314f9cc762e44c53b60248201526044810192909252517388887ed889e776bcbe2f0f9932ecfabcdfcd1820926329965a1d926064808201939182900301818387803b1580156200011457600080fd5b505af115801562000129573d6000803e3d6000fd5b5050604080516001808252818301909252606093509150602080830190803683370190505090506000816000815181106200016057fe5b6001600160a01b03909216602092830291909101820152604051630809469f60e11b815260048101828152835160248301528351730888000000000000000000000000000000000001936310128d3e938693928392604490920191858101910280838360005b83811015620001e0578181015183820152602001620001c6565b5050505090500192505050600060405180830381600087803b1580156200020657600080fd5b505af11580156200021b573d6000803e3d6000fd5b50506040805163c55b6bb760e01b8152306004820152600060248201819052915161011160931b945063c55b6bb793506044808301939282900301818387803b1580156200026857600080fd5b505af11580156200027d573d6000803e3d6000fd5b5050604080516364efb22b60e01b815230600482015290516000935061011160931b92506364efb22b91602480820192602092909190829003018186803b158015620002c857600080fd5b505afa158015620002dd573d6000803e3d6000fd5b505050506040513d6020811015620002f457600080fd5b50516001600160a01b03161462000352576040805162461bcd60e51b815260206004820152601560248201527f726571756972652061646d696e203d3d206e756c6c0000000000000000000000604482015290519081900360640190fd5b50505061127580620003656000396000f3fe6080604052600436106100f25760003560e01c806343b0215f1161008a5780638129fc1c116100595780638129fc1c1461037f5780638c4ac9221461038757806397610f301461039c578063b2deb677146103b1576100f2565b806343b0215f146102da57806347ccca02146103405780634f66168514610355578063555d3e631461036a576100f2565b80631d073a7a116100c65780631d073a7a1461027157806327a52c1114610286578063283c01fb1461029b5780632e1a7d4d146102b0576100f2565b806223de29146100f75780630fdbbe57146101f0578063158ef93e1461022157806317dde23d1461024a575b600080fd5b34801561010357600080fd5b506101ee600480360360c081101561011a57600080fd5b6001600160a01b03823581169260208101358216926040820135909216916060820135919081019060a08101608082013564010000000081111561015d57600080fd5b82018360208201111561016f57600080fd5b8035906020019184600183028401116401000000008311171561019157600080fd5b9193909290916020810190356401000000008111156101af57600080fd5b8201836020820111156101c157600080fd5b803590602001918460018302840111640100000000831117156101e357600080fd5b5090925090506103c6565b005b3480156101fc57600080fd5b5061020561051a565b604080516001600160a01b039092168252519081900360200190f35b34801561022d57600080fd5b5061023661052e565b604080519115158252519081900360200190f35b34801561025657600080fd5b5061025f610537565b60408051918252519081900360200190f35b34801561027d57600080fd5b5061020561053d565b34801561029257600080fd5b5061025f610555565b3480156102a757600080fd5b5061025f61055b565b3480156102bc57600080fd5b5061025f600480360360208110156102d357600080fd5b5035610568565b3480156102e657600080fd5b5061030d600480360360208110156102fd57600080fd5b50356001600160a01b0316610798565b604080519687526020870195909552858501939093529015156060850152608084015260a0830152519081900360c00190f35b34801561034c57600080fd5b506102056107d1565b34801561036157600080fd5b5061025f6107e0565b34801561037657600080fd5b506102056107ef565b6101ee6107f8565b34801561039357600080fd5b5061025f610951565b3480156103a857600080fd5b50610205610957565b3480156103bd57600080fd5b5061025f610963565b60055460ff166104075760405162461bcd60e51b81526004018080602001828103825260278152602001806110ea6027913960400191505060405180910390fd5b6001600160a01b038616301461044e5760405162461bcd60e51b81526004018080602001828103825260258152602001806110426025913960400191505060405180910390fd5b60055461010090046001600160a01b0316331461049c5760405162461bcd60e51b81526004018080602001828103825260248152602001806110946024913960400191505060405180910390fd5b600085116104f1576040805162461bcd60e51b815260206004820152601a60248201527f464345786368616e67653a20616d6f756e74206973207a65726f000000000000604482015290519081900360640190fd5b8215610506576105018786610969565b610510565b6105108786610af8565b5050505050505050565b60055461010090046001600160a01b031681565b60055460ff1681565b60015481565b73088800000000000000000000000000000000000181565b60045481565b683635c9adc5dea0000081565b60055460009060ff166105ac5760405162461bcd60e51b81526004018080602001828103825260278152602001806110ea6027913960400191505060405180910390fd5b33600090815260208190526040902080546105f85760405162461bcd60e51b815260040180806020018281038252602a815260200180611190602a913960400191505060405180910390fd5b80548311156106385760405162461bcd60e51b815260040180806020018281038252602d815260200180611067602d913960400191505060405180910390fd5b610640610bf8565b600061067b8260010154610675670de0b6b3a764000061066f6004548760000154610d1c90919063ffffffff16565b90610d7e565b90610de5565b90506106878185610e42565b4710156106c55760405162461bcd60e51b815260040180806020018281038252602d8152602001806111e7602d913960400191505060405180910390fd5b336108fc6106d38387610e42565b6040518115909202916000818181858888f193505050501580156106fb573d6000803e3d6000fd5b506003546107099085610de5565b60035581546107189085610de5565b80835560045461073691670de0b6b3a76400009161066f9190610d1c565b6001830155600582015461074a9082610e42565b6005830155610757610e9c565b6040805185815260208101839052815133927ff279e6a1f5e320cca91135676d9cb6e44ca8a08c0b88342bcdb1144f6511b568928290030190a29392505050565b60006020819052908152604090208054600182015460028301546003840154600485015460059095015493949293919260ff9091169186565b6006546001600160a01b031681565b6a115eec47f6cf7e3500000081565b61011160931b81565b600660009054906101000a90046001600160a01b03166001600160a01b0316638f32d59b6040518163ffffffff1660e01b8152600401602060405180830381600087803b15801561084857600080fd5b505af115801561085c573d6000803e3d6000fd5b505050506040513d602081101561087257600080fd5b50516108af5760405162461bcd60e51b81526004018080602001828103825260328152602001806110b86032913960400191505060405180910390fd5b60055460ff16156108f15760405162461bcd60e51b81526004018080602001828103825260298152602001806111116029913960400191505060405180910390fd5b6a115eec47f6cf7e35000000341461093a5760405162461bcd60e51b815260040180806020018281038252602d8152602001806111ba602d913960400191505060405180910390fd5b6005805460ff1916600117905561094f610e9c565b565b60035481565b600261011160931b0181565b60025481565b6001600160a01b0382166000908152602081905260409020610989610bf8565b805460009015610a39576109c08260010154610675670de0b6b3a764000061066f6004548760000154610d1c90919063ffffffff16565b905080471015610a015760405162461bcd60e51b815260040180806020018281038252602c815260200180611214602c913960400191505060405180910390fd5b6040516001600160a01b0385169082156108fc029083906000818181858888f19350505050158015610a37573d6000803e3d6000fd5b505b600354610a469084610e42565b6003558154610a559084610e42565b82556002820154610a669084610e42565b60028301556004548254610a8791670de0b6b3a76400009161066f91610d1c565b60018301556005820154610a9b9082610e42565b6005830155610aaa8483610f0a565b610ab2610e9c565b60408051828152905184916001600160a01b038716917f90890809c654f11d6e72a28fa60149770a0d11ec6c92319d6ceb2bb0a4ea1a159181900360200190a350505050565b6001600160a01b0382166000908152602081905260409020610b18610bf8565b81471015610b575760405162461bcd60e51b815260040180806020018281038252603581526020018061115b6035913960400191505060405180910390fd5b6040516001600160a01b0384169083156108fc029084906000818181858888f19350505050158015610b8d573d6000803e3d6000fd5b506002810154610b9d9083610e42565b6002820155610bac8382610f0a565b610bb4610e9c565b6040805183815290516001600160a01b038516917f8e3efd5fbe6bf23ef93bbe16f31d2ede957e78c35c4230710bd6e1a31c7a640f919081900360200190a2505050565b6001541561094f576000479050600261011160931b016001600160a01b0316632e1a7d4d6001546040518263ffffffff1660e01b815260040180828152602001915050600060405180830381600087803b158015610c5557600080fd5b505af1158015610c69573d6000803e3d6000fd5b505050506000610c828247610de590919063ffffffff16565b90506000610c9b60015483610de590919063ffffffff16565b90506000600354118015610caf5750600081115b15610cde57600354610cda90610cd19061066f84670de0b6b3a7640000610d1c565b60045490610e42565b6004555b600254604051829143917f604b365b22dbda1203004fba26c477ed19c49ad01cadf6eedf5f17f52ebbae1890600090a4505060006001555043600255565b600082610d2b57506000610d78565b82820282848281610d3857fe5b0414610d755760405162461bcd60e51b815260040180806020018281038252602181526020018061113a6021913960400191505060405180910390fd5b90505b92915050565b6000808211610dd4576040805162461bcd60e51b815260206004820152601a60248201527f536166654d6174683a206469766973696f6e206279207a65726f000000000000604482015290519081900360640190fd5b818381610ddd57fe5b049392505050565b600082821115610e3c576040805162461bcd60e51b815260206004820152601e60248201527f536166654d6174683a207375627472616374696f6e206f766572666c6f770000604482015290519081900360640190fd5b50900390565b600082820183811015610d75576040805162461bcd60e51b815260206004820152601b60248201527f536166654d6174683a206164646974696f6e206f766572666c6f770000000000604482015290519081900360640190fd5b471561094f5747600155436002556040805163b6b55f2560e01b81524760048201529051600261011160931b019163b6b55f2591602480830192600092919082900301818387803b158015610ef057600080fd5b505af1158015610f04573d6000803e3d6000fd5b50505050565b600381015460ff16158015610f2c5750683635c9adc5dea00000816002015410155b1561103d57600654604080516336a100d560e01b81526001600160a01b038581166004830152600160248301819052604483015260a06064830152601060a48301526f21b7b733363abc1023bab0b93234b0b760811b60c483015260e06084830152600060e4830181905292519316926336a100d59261012480840193602093929083900390910190829087803b158015610fc657600080fd5b505af1158015610fda573d6000803e3d6000fd5b505050506040513d6020811015610ff057600080fd5b50516004820181905560038201805460ff191660011790556040516001600160a01b038416907f042267a2a1d1f6745fce1211773b221aa27466879143626c17c60e279ace522990600090a35b505056fe464345786368616e67653a206465706f736974206e6f7420746f20464345786368616e6765464345786368616e67653a2075736572206465706f736974656420464320697320696e73756666696369656e74464345786368616e67653a20746f6b656e207265636569766564206973206e6f74204643464345786368616e67653a2065786368616e676520636f6e7472616374206973206e6f74206f776e6572206f66204352434e464345786368616e67653a20636f6e7472616374206973206e6f7420696e697469616c697a6564464345786368616e67653a20636f6e747261637420686173206265656e20696e697469616c697a6564536166654d6174683a206d756c7469706c69636174696f6e206f766572666c6f77464345786368616e676520696e7374616e742065786368616e67653a206366782062616c616e636520696e73756666696369656e74464345786368616e67653a2075736572204643206465706f73697420616d6f756e74206973207a65726f464345786368616e67653a20696e6974616c697a652076616c7565206973206e6f74203231206d696c6c696f6e464345786368616e67652077697468647261773a206366782062616c616e636520696e73756666696369656e74464345786368616e6765206465706f7369743a206366782062616c616e636520696e73756666696369656e74a2646970667358221220d12f9f79002e70310d40b1a0ce3eaddb57304cc665a25bd75a9353a0506d658b64736f6c63430007010033";

    public static final String FUNC_SPONSOR = "SPONSOR";

    public static final String FUNC_STAKING = "STAKING";

    public static final String FUNC_ACCCFXPERFC = "accCfxPerFc";

    public static final String FUNC_ADMINCONTROL = "adminControl";

    public static final String FUNC_FCADDR = "fcAddr";

    public static final String FUNC_FCCAP = "fcCap";

    public static final String FUNC_FCSUPPLY = "fcSupply";

    public static final String FUNC_GRANTREQUIRED = "grantRequired";

    public static final String FUNC_INITIALIZE = "initialize";

    public static final String FUNC_INITIALIZED = "initialized";

    public static final String FUNC_LASTSTAKINGAMOUNT = "lastStakingAmount";

    public static final String FUNC_LASTSTAKINGBLOCKNUMBER = "lastStakingBlockNumber";

    public static final String FUNC_NFT = "nft";

    public static final String FUNC_TOKENSRECEIVED = "tokensReceived";

    public static final String FUNC_USERINFOS = "userInfos";

    public static final String FUNC_WITHDRAW = "withdraw";

    public static final Event DEPOSIT_EVENT = new Event("Deposit", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event INSTANTEXCHANGE_EVENT = new Event("InstantExchange", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event NFTGRANTED_EVENT = new Event("NftGranted", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>(true) {}));
    ;

    public static final Event PROFIT_EVENT = new Event("Profit", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>(true) {}, new TypeReference<Uint256>(true) {}, new TypeReference<Uint256>(true) {}));
    ;

    public static final Event WITHDRAW_EVENT = new Event("Withdraw", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
    ;

    @Deprecated
    protected FCExchange(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected FCExchange(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected FCExchange(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected FCExchange(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public List<DepositEventResponse> getDepositEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(DEPOSIT_EVENT, transactionReceipt);
        ArrayList<DepositEventResponse> responses = new ArrayList<DepositEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            DepositEventResponse typedResponse = new DepositEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.profit = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<DepositEventResponse> depositEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, DepositEventResponse>() {
            @Override
            public DepositEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(DEPOSIT_EVENT, log);
                DepositEventResponse typedResponse = new DepositEventResponse();
                typedResponse.log = log;
                typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.profit = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<DepositEventResponse> depositEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(DEPOSIT_EVENT));
        return depositEventFlowable(filter);
    }

    public List<InstantExchangeEventResponse> getInstantExchangeEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(INSTANTEXCHANGE_EVENT, transactionReceipt);
        ArrayList<InstantExchangeEventResponse> responses = new ArrayList<InstantExchangeEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            InstantExchangeEventResponse typedResponse = new InstantExchangeEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<InstantExchangeEventResponse> instantExchangeEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, InstantExchangeEventResponse>() {
            @Override
            public InstantExchangeEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(INSTANTEXCHANGE_EVENT, log);
                InstantExchangeEventResponse typedResponse = new InstantExchangeEventResponse();
                typedResponse.log = log;
                typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<InstantExchangeEventResponse> instantExchangeEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(INSTANTEXCHANGE_EVENT));
        return instantExchangeEventFlowable(filter);
    }

    public List<NftGrantedEventResponse> getNftGrantedEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(NFTGRANTED_EVENT, transactionReceipt);
        ArrayList<NftGrantedEventResponse> responses = new ArrayList<NftGrantedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            NftGrantedEventResponse typedResponse = new NftGrantedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.userAddr = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<NftGrantedEventResponse> nftGrantedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, NftGrantedEventResponse>() {
            @Override
            public NftGrantedEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(NFTGRANTED_EVENT, log);
                NftGrantedEventResponse typedResponse = new NftGrantedEventResponse();
                typedResponse.log = log;
                typedResponse.userAddr = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.tokenId = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<NftGrantedEventResponse> nftGrantedEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(NFTGRANTED_EVENT));
        return nftGrantedEventFlowable(filter);
    }

    public List<ProfitEventResponse> getProfitEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(PROFIT_EVENT, transactionReceipt);
        ArrayList<ProfitEventResponse> responses = new ArrayList<ProfitEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            ProfitEventResponse typedResponse = new ProfitEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.lastBlockNumber = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.currentBlockNumber = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
            typedResponse.profit = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<ProfitEventResponse> profitEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, ProfitEventResponse>() {
            @Override
            public ProfitEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(PROFIT_EVENT, log);
                ProfitEventResponse typedResponse = new ProfitEventResponse();
                typedResponse.log = log;
                typedResponse.lastBlockNumber = (BigInteger) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.currentBlockNumber = (BigInteger) eventValues.getIndexedValues().get(1).getValue();
                typedResponse.profit = (BigInteger) eventValues.getIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<ProfitEventResponse> profitEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(PROFIT_EVENT));
        return profitEventFlowable(filter);
    }

    public List<WithdrawEventResponse> getWithdrawEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(WITHDRAW_EVENT, transactionReceipt);
        ArrayList<WithdrawEventResponse> responses = new ArrayList<WithdrawEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            WithdrawEventResponse typedResponse = new WithdrawEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
            typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.profit = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Flowable<WithdrawEventResponse> withdrawEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(new Function<Log, WithdrawEventResponse>() {
            @Override
            public WithdrawEventResponse apply(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(WITHDRAW_EVENT, log);
                WithdrawEventResponse typedResponse = new WithdrawEventResponse();
                typedResponse.log = log;
                typedResponse.user = (String) eventValues.getIndexedValues().get(0).getValue();
                typedResponse.amount = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.profit = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public Flowable<WithdrawEventResponse> withdrawEventFlowable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(WITHDRAW_EVENT));
        return withdrawEventFlowable(filter);
    }

    public RemoteFunctionCall<String> SPONSOR() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_SPONSOR, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> STAKING() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_STAKING, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> accCfxPerFc() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ACCCFXPERFC, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> adminControl() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_ADMINCONTROL, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> fcAddr() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_FCADDR, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> fcCap() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_FCCAP, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> fcSupply() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_FCSUPPLY, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> grantRequired() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GRANTREQUIRED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<TransactionReceipt> initialize() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_INITIALIZE, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> initialized() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_INITIALIZED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<BigInteger> lastStakingAmount() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_LASTSTAKINGAMOUNT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> lastStakingBlockNumber() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_LASTSTAKINGBLOCKNUMBER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<String> nft() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_NFT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> tokensReceived(String param0, String from, String to, BigInteger amount, byte[] userData, byte[] param5) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_TOKENSRECEIVED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0), 
                new org.web3j.abi.datatypes.Address(160, from), 
                new org.web3j.abi.datatypes.Address(160, to), 
                new org.web3j.abi.datatypes.generated.Uint256(amount), 
                new org.web3j.abi.datatypes.DynamicBytes(userData), 
                new org.web3j.abi.datatypes.DynamicBytes(param5)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Tuple6<BigInteger, BigInteger, BigInteger, Boolean, BigInteger, BigInteger>> userInfos(String param0) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_USERINFOS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}, new TypeReference<Bool>() {}, new TypeReference<Uint256>() {}, new TypeReference<Uint256>() {}));
        return new RemoteFunctionCall<Tuple6<BigInteger, BigInteger, BigInteger, Boolean, BigInteger, BigInteger>>(function,
                new Callable<Tuple6<BigInteger, BigInteger, BigInteger, Boolean, BigInteger, BigInteger>>() {
                    @Override
                    public Tuple6<BigInteger, BigInteger, BigInteger, Boolean, BigInteger, BigInteger> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple6<BigInteger, BigInteger, BigInteger, Boolean, BigInteger, BigInteger>(
                                (BigInteger) results.get(0).getValue(), 
                                (BigInteger) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (Boolean) results.get(3).getValue(), 
                                (BigInteger) results.get(4).getValue(), 
                                (BigInteger) results.get(5).getValue());
                    }
                });
    }

    public RemoteFunctionCall<TransactionReceipt> withdraw(BigInteger amount) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_WITHDRAW, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(amount)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static FCExchange load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new FCExchange(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static FCExchange load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new FCExchange(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static FCExchange load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new FCExchange(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static FCExchange load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new FCExchange(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<FCExchange> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider, String _fcAddr, String _nftAddr) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _fcAddr), 
                new org.web3j.abi.datatypes.Address(160, _nftAddr)));
        return deployRemoteCall(FCExchange.class, web3j, credentials, contractGasProvider, BINARY, encodedConstructor);
    }

    public static RemoteCall<FCExchange> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider, String _fcAddr, String _nftAddr) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _fcAddr), 
                new org.web3j.abi.datatypes.Address(160, _nftAddr)));
        return deployRemoteCall(FCExchange.class, web3j, transactionManager, contractGasProvider, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<FCExchange> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _fcAddr, String _nftAddr) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _fcAddr), 
                new org.web3j.abi.datatypes.Address(160, _nftAddr)));
        return deployRemoteCall(FCExchange.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    @Deprecated
    public static RemoteCall<FCExchange> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _fcAddr, String _nftAddr) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, _fcAddr), 
                new org.web3j.abi.datatypes.Address(160, _nftAddr)));
        return deployRemoteCall(FCExchange.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static class DepositEventResponse extends BaseEventResponse {
        public String user;

        public BigInteger amount;

        public BigInteger profit;
    }

    public static class InstantExchangeEventResponse extends BaseEventResponse {
        public String user;

        public BigInteger amount;
    }

    public static class NftGrantedEventResponse extends BaseEventResponse {
        public String userAddr;

        public BigInteger tokenId;
    }

    public static class ProfitEventResponse extends BaseEventResponse {
        public BigInteger lastBlockNumber;

        public BigInteger currentBlockNumber;

        public BigInteger profit;
    }

    public static class WithdrawEventResponse extends BaseEventResponse {
        public String user;

        public BigInteger amount;

        public BigInteger profit;
    }
}
