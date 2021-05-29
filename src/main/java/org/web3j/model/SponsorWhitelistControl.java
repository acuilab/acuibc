package org.web3j.model;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
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
public class SponsorWhitelistControl extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b506103c8806100206000396000f3fe6080604052600436106100c25760003560e01c80638382c3a71161007f578063d2932db611610059578063d2932db6146100c7578063d47e9a57146102eb578063d665f9dd146102eb578063e66c1bea1461035b576100c2565b80638382c3a714610239578063b3b28fac146102eb578063b6b3527214610320576100c2565b806310128d3e146100c7578063217e055b1461017957806322effe841461017957806333a1af31146102395780633e3e64281461028857806379b47faa146102b4575b600080fd5b3480156100d357600080fd5b50610177600480360360208110156100ea57600080fd5b81019060208101813564010000000081111561010557600080fd5b82018360208201111561011757600080fd5b8035906020019184602083028401116401000000008311171561013957600080fd5b91908080602002602001604051908101604052809392919081815260200183836020028082843760009201919091525092955061037d945050505050565b005b34801561018557600080fd5b506101776004803603604081101561019c57600080fd5b6001600160a01b0382351691908101906040810160208201356401000000008111156101c757600080fd5b8201836020820111156101d957600080fd5b803590602001918460208302840111640100000000831117156101fb57600080fd5b919080806020026020016040519081016040528093929190818152602001838360200280828437600092019190915250929550610380945050505050565b34801561024557600080fd5b5061026c6004803603602081101561025c57600080fd5b50356001600160a01b0316610384565b604080516001600160a01b039092168252519081900360200190f35b6101776004803603604081101561029e57600080fd5b506001600160a01b038135169060200135610380565b3480156102c057600080fd5b506102d76004803603602081101561025c57600080fd5b604080519115158252519081900360200190f35b3480156102f757600080fd5b5061030e6004803603602081101561025c57600080fd5b60408051918252519081900360200190f35b34801561032c57600080fd5b506102d76004803603604081101561034357600080fd5b506001600160a01b038135811691602001351661038a565b6101776004803603602081101561037157600080fd5b50356001600160a01b03165b50565b5050565b50600090565b60009291505056fea26469706673582212203c2eb2534d7695aecdabaed0c999b6cb6809c1cc72d0cb3fd8f96399ca58252c64736f6c63430007010033";

    public static final String FUNC_ADDPRIVILEGE = "addPrivilege";

    public static final String FUNC_ADDPRIVILEGEBYADMIN = "addPrivilegeByAdmin";

    public static final String FUNC_GETSPONSORFORCOLLATERAL = "getSponsorForCollateral";

    public static final String FUNC_GETSPONSORFORGAS = "getSponsorForGas";

    public static final String FUNC_GETSPONSOREDBALANCEFORCOLLATERAL = "getSponsoredBalanceForCollateral";

    public static final String FUNC_GETSPONSOREDBALANCEFORGAS = "getSponsoredBalanceForGas";

    public static final String FUNC_GETSPONSOREDGASFEEUPPERBOUND = "getSponsoredGasFeeUpperBound";

    public static final String FUNC_ISALLWHITELISTED = "isAllWhitelisted";

    public static final String FUNC_ISWHITELISTED = "isWhitelisted";

    public static final String FUNC_REMOVEPRIVILEGE = "removePrivilege";

    public static final String FUNC_REMOVEPRIVILEGEBYADMIN = "removePrivilegeByAdmin";

    public static final String FUNC_SETSPONSORFORCOLLATERAL = "setSponsorForCollateral";

    public static final String FUNC_SETSPONSORFORGAS = "setSponsorForGas";

    @Deprecated
    protected SponsorWhitelistControl(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected SponsorWhitelistControl(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected SponsorWhitelistControl(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected SponsorWhitelistControl(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> addPrivilege(List<String> param0) {
        final Function function = new Function(
                FUNC_ADDPRIVILEGE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(param0, org.web3j.abi.datatypes.Address.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addPrivilegeByAdmin(String contractAddr, List<String> addresses) {
        final Function function = new Function(
                FUNC_ADDPRIVILEGEBYADMIN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, contractAddr), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(addresses, org.web3j.abi.datatypes.Address.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> getSponsorForCollateral(String contractAddr) {
        final Function function = new Function(FUNC_GETSPONSORFORCOLLATERAL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, contractAddr)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> getSponsorForGas(String contractAddr) {
        final Function function = new Function(FUNC_GETSPONSORFORGAS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, contractAddr)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<BigInteger> getSponsoredBalanceForCollateral(String contractAddr) {
        final Function function = new Function(FUNC_GETSPONSOREDBALANCEFORCOLLATERAL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, contractAddr)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getSponsoredBalanceForGas(String contractAddr) {
        final Function function = new Function(FUNC_GETSPONSOREDBALANCEFORGAS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, contractAddr)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<BigInteger> getSponsoredGasFeeUpperBound(String contractAddr) {
        final Function function = new Function(FUNC_GETSPONSOREDGASFEEUPPERBOUND, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, contractAddr)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}));
        return executeRemoteCallSingleValueReturn(function, BigInteger.class);
    }

    public RemoteFunctionCall<Boolean> isAllWhitelisted(String contractAddr) {
        final Function function = new Function(FUNC_ISALLWHITELISTED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, contractAddr)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<Boolean> isWhitelisted(String contractAddr, String user) {
        final Function function = new Function(FUNC_ISWHITELISTED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, contractAddr), 
                new org.web3j.abi.datatypes.Address(160, user)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<TransactionReceipt> removePrivilege(List<String> param0) {
        final Function function = new Function(
                FUNC_REMOVEPRIVILEGE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(param0, org.web3j.abi.datatypes.Address.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> removePrivilegeByAdmin(String contractAddr, List<String> addresses) {
        final Function function = new Function(
                FUNC_REMOVEPRIVILEGEBYADMIN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, contractAddr), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(addresses, org.web3j.abi.datatypes.Address.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setSponsorForCollateral(String contractAddr) {
        final Function function = new Function(
                FUNC_SETSPONSORFORCOLLATERAL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, contractAddr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> setSponsorForGas(String contractAddr, BigInteger upperBound) {
        final Function function = new Function(
                FUNC_SETSPONSORFORGAS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, contractAddr), 
                new org.web3j.abi.datatypes.generated.Uint256(upperBound)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static SponsorWhitelistControl load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new SponsorWhitelistControl(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static SponsorWhitelistControl load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new SponsorWhitelistControl(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static SponsorWhitelistControl load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new SponsorWhitelistControl(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static SponsorWhitelistControl load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new SponsorWhitelistControl(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<SponsorWhitelistControl> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(SponsorWhitelistControl.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<SponsorWhitelistControl> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(SponsorWhitelistControl.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<SponsorWhitelistControl> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(SponsorWhitelistControl.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<SponsorWhitelistControl> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(SponsorWhitelistControl.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
