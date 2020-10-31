package com.github.chain5j.engine;

import com.github.chain5j.protocol.Chain5j;
import com.github.chain5j.protocol.core.DefaultBlockParameterName;
import com.github.chain5j.protocol.core.Request;
import com.github.chain5j.protocol.core.methods.response.*;
import com.github.chain5j.abi.FunctionEncoder;
import com.github.chain5j.abi.FunctionReturnDecoder;
import com.github.chain5j.abi.TypeReference;
import com.github.chain5j.abi.datatypes.Function;
import com.github.chain5j.abi.datatypes.Type;
import com.github.chain5j.abi.datatypes.Utf8String;
import com.github.chain5j.crypto.Hash;
import com.github.chain5j.crypto.RawTransaction;
import com.github.chain5j.crypto.TransactionDecoder;
import com.github.chain5j.protocol.core.methods.response.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2019-05-31 17:10
 * @Copyright Copyright@2019
 */
public class TransactionEngine {

    /**
     * 获取交易记录
     *
     * @param hash
     * @throws IOException
     */
    public static Transaction getTransaction(Chain5j chain5j, String hash) throws Exception {
        Request<?, RpcTransaction> rpcTransactionRequest = chain5j.getTransactionByHash(hash);
        RpcTransaction rpcTransaction = rpcTransactionRequest.send();
        if (rpcTransaction.hasError()) {
            throw new Exception(rpcTransaction.getError().toString());
        }
        Optional<Transaction> transactionOptional = rpcTransaction.getTransaction();
        if (transactionOptional.isPresent()) {
            Transaction transaction = transactionOptional.get();
            return transaction;
        }
        return null;
    }

    /**
     * 获取交易收据【判断交易是否成功，是需要看这个函数】
     *
     * @param hash
     * @return
     * @throws IOException
     */
    public static TransactionReceipt getTransactionReceipt(Chain5j chain5j, String hash) throws Exception {
        Request<?, RpcGetTransactionReceipt> rpcGetTransactionReceiptRequest = chain5j.getTransactionReceipt(hash);
        RpcGetTransactionReceipt rpcGetTransactionReceipt = rpcGetTransactionReceiptRequest.send();
        if (rpcGetTransactionReceipt.hasError()) {
            throw new Exception(rpcGetTransactionReceipt.getError().toString());
        }
        Optional<TransactionReceipt> transactionReceiptOptional = rpcGetTransactionReceipt.getTransactionReceipt();
        if (transactionReceiptOptional.isPresent()) {
            TransactionReceipt transactionReceipt = transactionReceiptOptional.get();
            return transactionReceipt;
        }
        return null;
    }

    /**
     * 交易解析
     *
     * @param txRaw
     * @return
     * @throws Exception
     */
    public static RawTransaction decodeRawTransaction(String txRaw) {
        return TransactionDecoder.decode(txRaw);
    }

    /**
     * 获取交易记录的hash值
     *
     * @param signData
     * @return
     */
    public static String getTxHash(String signData) {
        String sha3 = Hash.sha3(signData);
        return sha3;
    }

    /**
     * Description: 解析合约交易
     * </p>
     *
     * @param chain5j
     * @param from
     * @param to
     * @param method
     * @param inputParameters
     * @param outputParameters
     * @return java.util.List<com.github.chain5j.abi.datatypes.Type>
     * @Author: xwc1125
     * @Date: 2019-05-31 19:21:56
     */
    public static List<Type> decodeTransaction(Chain5j chain5j, String from, String to, String method, List<Type> inputParameters, List<TypeReference<?>> outputParameters) throws Exception {
        if (outputParameters == null || outputParameters.size() == 0) {
            outputParameters = Collections.<TypeReference<?>>emptyList();
        }
        if (inputParameters == null || inputParameters.size() == 0) {
            inputParameters = Collections.<Type>emptyList();
        }
        Function function = new Function(method, inputParameters, outputParameters);
        String dataHex = FunctionEncoder.encode(function);
        com.github.chain5j.protocol.core.methods.request.Transaction callTransaction = SignEngine.getCallTransaction(from, to, dataHex);
        CompletableFuture<CallContract> rpcCallCompletableFuture = chain5j.callContract(callTransaction, DefaultBlockParameterName.LATEST).sendAsync();
        CallContract callContract = rpcCallCompletableFuture.get();
        if (callContract.hasError()) {
            throw new Exception(callContract.getError().toString());
        }
        String response = callContract.getValue();
        List<Type> decode = new ArrayList<>();
        List<TypeReference<Type>> outputParameters1 = function.getOutputParameters();
        if (outputParameters1 != null && outputParameters1.size() > 0) {
            decode = FunctionReturnDecoder.decode(response, outputParameters1);
        } else {
            Utf8String utf8String = new Utf8String(response);
            decode.add(utf8String);
        }
        return decode;
    }
}
