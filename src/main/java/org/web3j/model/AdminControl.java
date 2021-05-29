package org.web3j.model;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
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
public class AdminControl extends Contract {
    public static final String BINARY = "608060405234801561001057600080fd5b50610112806100206000396000f3fe6080604052348015600f57600080fd5b5060043610603b5760003560e01c8062f55d9d14604057806364efb22b146065578063c55b6bb71460a4575b600080fd5b606360048036036020811015605457600080fd5b50356001600160a01b031660cf565b005b608860048036036020811015607957600080fd5b50356001600160a01b031660d2565b604080516001600160a01b039092168252519081900360200190f35b60636004803603604081101560b857600080fd5b506001600160a01b038135811691602001351660d8565b50565b50600090565b505056fea264697066735822122073bbb0116320b45e2639effab74a1b6e836132d05bad9cf237529417df881e0e64736f6c63430007010033";

    public static final String FUNC_DESTROY = "destroy";

    public static final String FUNC_GETADMIN = "getAdmin";

    public static final String FUNC_SETADMIN = "setAdmin";

    @Deprecated
    protected AdminControl(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected AdminControl(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected AdminControl(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected AdminControl(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public RemoteFunctionCall<TransactionReceipt> destroy(String contractAddr) {
        final Function function = new Function(
                FUNC_DESTROY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, contractAddr)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> getAdmin(String contractAddr) {
        final Function function = new Function(FUNC_GETADMIN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, contractAddr)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> setAdmin(String contractAddr, String newAdmin) {
        final Function function = new Function(
                FUNC_SETADMIN, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, contractAddr), 
                new org.web3j.abi.datatypes.Address(160, newAdmin)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    @Deprecated
    public static AdminControl load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new AdminControl(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static AdminControl load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new AdminControl(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static AdminControl load(String contractAddress, Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return new AdminControl(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static AdminControl load(String contractAddress, Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new AdminControl(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static RemoteCall<AdminControl> deploy(Web3j web3j, Credentials credentials, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(AdminControl.class, web3j, credentials, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<AdminControl> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(AdminControl.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<AdminControl> deploy(Web3j web3j, TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return deployRemoteCall(AdminControl.class, web3j, transactionManager, contractGasProvider, BINARY, "");
    }

    @Deprecated
    public static RemoteCall<AdminControl> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(AdminControl.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }
}
