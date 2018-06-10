package com.kau.minseop.pointshare.contract;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.4.0.
 */
public class CardListTest extends Contract {
    private static final String BINARY = "608060405234801561001057600080fd5b50610570806100206000396000f3006080604052600436106100565763ffffffff7c0100000000000000000000000000000000000000000000000000000000600035041663122772de811461005b5780636503b2ad146100b6578063eff32daf1461015f575b600080fd5b34801561006757600080fd5b506040805160206004803580820135601f81018490048402850184019095528484526100b49436949293602493928401919081908401838280828437509497506101779650505050505050565b005b3480156100c257600080fd5b506100ce6004356102f4565b6040518083600160a060020a0316600160a060020a0316815260200180602001828103825283818151815260200191508051906020019080838360005b8381101561012357818101518382015260200161010b565b50505050905090810190601f1680156101505780820380516001836020036101000a031916815260200191505b50935050505060405180910390f35b34801561016b57600080fd5b506100ce6004356103b7565b604080518082019091523381526020808201838152600080546001818101808455838052865160029093027f290decd9548b62a8d60345a988386fc84ba6bc95484008f6362f93160ef3e56381018054600160a060020a039590951673ffffffffffffffffffffffffffffffffffffffff1990951694909417845594518051949792969195929461022f937f290decd9548b62a8d60345a988386fc84ba6bc95484008f6362f93160ef3e564019291909101906104a9565b5050500390507ffae8daae41f03d1ad2d8604550993ab7e8be69f5328ea3c42ef84c1de0f2866c8133846040518084815260200183600160a060020a0316600160a060020a0316815260200180602001828103825283818151815260200191508051906020019080838360005b838110156102b457818101518382015260200161029c565b50505050905090810190601f1680156102e15780820380516001836020036101000a031916815260200191505b5094505050505060405180910390a15050565b600080548290811061030257fe5b600091825260209182902060029182020180546001808301805460408051601f60001995841615610100029590950190921696909604928301879004870281018701909552818552600160a060020a03909216955091939091908301828280156103ad5780601f10610382576101008083540402835291602001916103ad565b820191906000526020600020905b81548152906001019060200180831161039057829003601f168201915b5050505050905082565b600060606000838154811015156103ca57fe5b600091825260208220600290910201548154600160a060020a039091169190859081106103f357fe5b9060005260206000209060020201600101808054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156104995780601f1061046e57610100808354040283529160200191610499565b820191906000526020600020905b81548152906001019060200180831161047c57829003601f168201915b5050505050905091509150915091565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106104ea57805160ff1916838001178555610517565b82800160010185558215610517579182015b828111156105175782518255916020019190600101906104fc565b50610523929150610527565b5090565b61054191905b80821115610523576000815560010161052d565b905600a165627a7a723058203e57a354bff21be162628bb5abda56eedf9200c90ba1688910b0e8a550a6160d0029";

    public static final String FUNC_SETCARDLIST = "setCardList";

    public static final String FUNC_CARDLISTS = "cardlists";

    public static final String FUNC_GETCARDLIST = "getCardList";

    public static final Event ADDCARDLIST_EVENT = new Event("AddCardList", 
            Arrays.<TypeReference<?>>asList(),
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint256>() {}, new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}));
    ;

    protected CardListTest(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected CardListTest(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public RemoteCall<TransactionReceipt> setCardList(String _qrcode) {
        final Function function = new Function(
                FUNC_SETCARDLIST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_qrcode)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple2<String, String>> cardlists(BigInteger param0) {
        final Function function = new Function(FUNC_CARDLISTS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}));
        return new RemoteCall<Tuple2<String, String>>(
                new Callable<Tuple2<String, String>>() {
                    @Override
                    public Tuple2<String, String> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<String, String>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue());
                    }
                });
    }

    public RemoteCall<Tuple2<String, String>> getCardList(int _id) {
        final Function function = new Function(FUNC_GETCARDLIST, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(_id)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Utf8String>() {}));
        return new RemoteCall<Tuple2<String, String>>(
                new Callable<Tuple2<String, String>>() {
                    @Override
                    public Tuple2<String, String> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<String, String>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue());
                    }
                });
    }

    public List<AddCardListEventResponse> getAddCardListEvents(TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = extractEventParametersWithLog(ADDCARDLIST_EVENT, transactionReceipt);
        ArrayList<AddCardListEventResponse> responses = new ArrayList<AddCardListEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            AddCardListEventResponse typedResponse = new AddCardListEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.owner = (String) eventValues.getNonIndexedValues().get(1).getValue();
            typedResponse.qrcode = (String) eventValues.getNonIndexedValues().get(2).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<AddCardListEventResponse> addCardListEventObservable(EthFilter filter) {
        return web3j.ethLogObservable(filter).map(new Func1<Log, AddCardListEventResponse>() {
            @Override
            public AddCardListEventResponse call(Log log) {
                Contract.EventValuesWithLog eventValues = extractEventParametersWithLog(ADDCARDLIST_EVENT, log);
                AddCardListEventResponse typedResponse = new AddCardListEventResponse();
                typedResponse.log = log;
                typedResponse.id = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse.owner = (String) eventValues.getNonIndexedValues().get(1).getValue();
                typedResponse.qrcode = (String) eventValues.getNonIndexedValues().get(2).getValue();
                return typedResponse;
            }
        });
    }

    public Observable<AddCardListEventResponse> addCardListEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(ADDCARDLIST_EVENT));
        return addCardListEventObservable(filter);
    }

    public static RemoteCall<CardListTest> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(CardListTest.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<CardListTest> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(CardListTest.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static CardListTest load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new CardListTest(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static CardListTest load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new CardListTest(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class AddCardListEventResponse {
        public Log log;

        public BigInteger id;

        public String owner;

        public String qrcode;
    }
}
