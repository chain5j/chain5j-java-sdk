package com.github.chain5j.engine;

import com.github.chain5j.abi.datatypes.generated.Uint256;
import com.github.chain5j.constant.TxType;
import com.github.chain5j.crypto.*;
import com.github.chain5j.protocol.Chain5j;
import com.github.chain5j.protocol.core.DefaultBlockParameter;
import com.github.chain5j.protocol.core.DefaultBlockParameterName;
import com.github.chain5j.protocol.core.Request;
import com.github.chain5j.protocol.core.methods.request.Transaction;
import com.github.chain5j.protocol.core.methods.response.CallContract;
import com.github.chain5j.protocol.core.methods.response.GetBalance;
import com.github.chain5j.protocol.core.methods.response.GetTransactionCount;
import com.github.chain5j.protocol.core.methods.response.SendTransaction;
import com.github.chain5j.protocol.http.HttpService;
import com.github.chain5j.abi.FunctionEncoder;
import com.github.chain5j.abi.TypeReference;
import com.github.chain5j.abi.datatypes.Address;
import com.github.chain5j.abi.datatypes.Function;
import com.github.chain5j.abi.datatypes.Type;
import com.github.chain5j.crypto.*;
import com.github.chain5j.protocol.core.methods.response.*;
import com.github.chain5j.utils.Numeric;

import java.math.BigInteger;
import java.security.SignatureException;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2019-05-31 17:12
 * @Copyright Copyright@2019
 */
public class SignEngine {

    /**
     * @param web3jUrl
     * @return
     */
    public static Chain5j getWeb3j(String web3jUrl) {
        return Chain5j.build(new HttpService(web3jUrl));
    }


    /**
     * 获取余额
     *
     * @param from
     * @return
     * @throws Exception
     */
    public static BigInteger getBalance(Chain5j chain5j, String from) throws Exception {
        CompletableFuture<GetBalance> future = chain5j.getBalance(from, DefaultBlockParameter.valueOf("latest")).sendAsync();
        GetBalance ethGetBalance = future.get();
        if (ethGetBalance.hasError()) {
            throw new Exception(ethGetBalance.getError().toString());
        }
        return ethGetBalance.getBalance();
    }

    /**
     * 获取合约余额
     *
     * @param address
     * @param contractAddress
     * @return
     * @throws Exception
     */
    public static BigInteger getTokenBalance(Chain5j chain5j, TxType txType, String address, String contractAddress) throws Exception {
        Address addressA = new Address(address);
        Function function = new Function("balanceOf", Arrays.<Type>asList(addressA), Collections.<TypeReference<?>>emptyList());
        Transaction transaction = getCallTransaction(address, contractAddress, function);
        CallContract callContract = chain5j.callContract(transaction, DefaultBlockParameterName.LATEST).sendAsync().get();
        if (callContract.hasError()) {
            throw new Exception(callContract.getError().toString());
        }
        String value = callContract.getValue();
        if (value.equals("0x")) {
            value = "0x0";
        }
        return Numeric.toBigInt(value);
    }

    /**
     * 获取gas
     *
     * @param from
     * @param to
     * @param contractAddress
     * @return
     * @throws Exception
     */
    public static BigInteger getGas(Chain5j chain5j, String from, String to, Interpreter interpreter, BigInteger value, String contractAddress) throws Exception {
        if (contractAddress == null || contractAddress.equals("")) {
            return new BigInteger("21000");
        }
        Address _to = new Address(to);
        Uint256 _value = new Uint256(value);
        Function function = new Function("transfer", Arrays.<Type>asList(_to, _value), Collections.<TypeReference<?>>emptyList());
        return getGas(chain5j, from, contractAddress, interpreter, function);
    }

    public static BigInteger getGas(Chain5j chain5j, String from, String to, Interpreter interpreter, Function function) throws Exception {
        String dataHex = "0x0";
        if (function != null) {
            dataHex = FunctionEncoder.encode(function);
        }
        return getGas(chain5j, from, to, interpreter, dataHex);
    }

    public static BigInteger getGas(Chain5j chain5j, String from, String to, Interpreter interpreter, String dataHex) throws Exception {
        if (to == null || to.equals("")) {
            return new BigInteger("21000");
        }
        Transaction transaction = new Transaction(from, to, interpreter, (BigInteger) null, (BigInteger) null, (BigInteger) null, BigInteger.ONE, dataHex, BigInteger.ZERO, "");
//        Request<?, RpcEstimateGas> ethEstimateGasRequest =chain5j.ethEstimateGas(transaction);
//        CompletableFuture<RpcEstimateGas> ethEstimateGasCompletableFuture = ethEstimateGasRequest.sendAsync();
//        RpcEstimateGas rpcEstimateGas = ethEstimateGasCompletableFuture.get();
//        if (rpcEstimateGas.hasError()) {
//            throw new Exception(rpcEstimateGas.getError().toString());
//        }
//        BigInteger estimateGas = rpcEstimateGas.getAmountUsed();
        return new BigInteger("4700000");
    }

    public static Transaction getCallTransaction(String from, String to, Function function) {
        String dataHex = "0x0";
        if (function != null) {
            dataHex = FunctionEncoder.encode(function);
        }
        return getCallTransaction(from, to, dataHex);
    }

    public static Transaction getCallTransaction(String from, String to, String dataHex) {
        return Transaction.createEvmCallTransaction(from, to, dataHex);
    }

    /**
     * 获取gasPrice
     *
     * @param isNon
     * @return
     * @throws Exception
     */
    public static BigInteger getGasPrice(Chain5j chain5j, boolean isNon) throws Exception {
        if (isNon) {
            return BigInteger.ZERO;
        }
//        Request<?, RpcGasPrice> ethGasPriceRequest = chain5j.ethGasPrice();
//        RpcGasPrice rpcGasPrice = ethGasPriceRequest.sendAsync().get();
//        if (rpcGasPrice.hasError()) {
//            throw new Exception(rpcGasPrice.getError().toString());
//        }
//        BigInteger gasPrice = rpcGasPrice.getGasPrice();
        return new BigInteger("20000000000");
    }

    /**
     * 获取Nonce
     *
     * @param from
     * @return
     * @throws Exception
     */
    public static BigInteger getNonce(Chain5j chain5j, String from) throws Exception {
        //getNonce
        chain5j.getTransactionCount(
                from, DefaultBlockParameterName.LATEST).sendAsync();
        GetTransactionCount getTransactionCount = chain5j.getTransactionCount(
                from, DefaultBlockParameterName.LATEST).sendAsync().get();
        if (getTransactionCount.hasError()) {
            throw new Exception(getTransactionCount.getError().toString());
        }
        BigInteger nonce = getTransactionCount.getTransactionCount();
        return nonce;
    }

    /**
     * 获取Nonce
     *
     * @param from
     * @param defaultBlockParameter DefaultBlockParameterName
     *                              EARLIEST("earliest"),
     *                              LATEST("latest"),
     *                              PENDING("pending");
     * @return
     * @throws Exception
     */
    public static BigInteger getNonce(Chain5j chain5j, String from, DefaultBlockParameter defaultBlockParameter) throws Exception {
        //getNonce
        GetTransactionCount getTransactionCount = chain5j.getTransactionCount(
                from, defaultBlockParameter).sendAsync().get();
        if (getTransactionCount.hasError()) {
            throw new Exception(getTransactionCount.getError().toString());
        }
        BigInteger nonce = getTransactionCount.getTransactionCount();
        return nonce;
    }


    //==================签名==================

    /**
     * 签名交易
     *
     * @param credentials 证书
     * @param nonce
     * @param gasPrice
     * @param gasLimit
     * @param to
     * @param value
     * @param fun
     * @return
     */
    public static SignRes sign(Credentials credentials, String from, String to, Interpreter interpreter, BigInteger nonce, BigInteger gasLimit, BigInteger gasPrice,
                               BigInteger value, Function fun, BigInteger deadline, String extra) {
        return sign(false, credentials, from, to, interpreter, nonce, gasLimit, gasPrice,
                value, fun, deadline, extra, 1);
    }

    /**
     * @param credentials
     * @param nonce
     * @param gasPrice
     * @param gasLimit
     * @param to
     * @param value
     * @param fun
     * @param chainId
     * @return
     */
    public static SignRes signEIP155(Credentials credentials, String from, String to, Interpreter interpreter, BigInteger nonce, BigInteger gasLimit, BigInteger gasPrice,
                                     BigInteger value, Function fun, BigInteger deadline, String extra, int chainId) {
        return sign(true, credentials, from, to, interpreter, nonce, gasLimit, gasPrice,
                value, fun, deadline, extra, chainId);
    }

    /**
     * @param isEIP155
     * @param credentials
     * @param nonce
     * @param gasPrice
     * @param gasLimit
     * @param to
     * @param value
     * @param fun
     * @param chainId
     * @return
     */
    public static SignRes sign(boolean isEIP155, Credentials credentials, String from, String to, Interpreter interpreter, BigInteger nonce, BigInteger gasLimit, BigInteger gasPrice,
                               BigInteger value, Function fun, BigInteger deadline, String extra, int chainId) {
        String data = null;
        if (fun != null) {
            data = FunctionEncoder.encode(fun);
        }
        return sign(isEIP155, credentials, from, to, interpreter, nonce, gasLimit, gasPrice,
                value, data, deadline, extra, chainId);
    }

    /**
     * Description: TODO
     * </p>
     *
     * @param isEIP155
     * @param credentials
     * @param from
     * @param to
     * @param interpreter
     * @param nonce
     * @param gasLimit
     * @param gasPrice
     * @param value
     * @param data        如果无，请填null
     * @param deadline
     * @param extra
     * @param chainId
     * @return com.github.chain5j.engine.SignRes
     * @Author: xwc1125
     * @Date: 2020-03-04 17:39:39
     */
    public static SignRes sign(boolean isEIP155, Credentials credentials, String from, String to, Interpreter interpreter, BigInteger nonce, BigInteger gasLimit, BigInteger gasPrice,
                               BigInteger value, String data, BigInteger deadline, String extra, int chainId) {
        RawTransaction rawTransaction = null;
        switch (interpreter) {
            case BASE:
                rawTransaction = RawTransaction.createBaseTransaction(from, to, nonce, gasLimit, gasPrice, value, data, deadline, extra);
            case ACCOUNT:
                rawTransaction = RawTransaction.createAccountTransaction(from, to, nonce, data, deadline, extra);
            case LOST:
                rawTransaction = RawTransaction.createLostTransaction(from, to, nonce, data, deadline, extra);
            case EVM:
                rawTransaction = RawTransaction.createEvmTransaction(from, nonce, gasLimit, gasPrice, value, data, deadline, extra);
            case CA:
                rawTransaction = RawTransaction.createCaTransaction(from, to, nonce, gasLimit, gasPrice, value, data, deadline, extra);
            case POE:
                rawTransaction = RawTransaction.createPoeTransaction(from, to, nonce, gasLimit, gasPrice, value, data, deadline, extra);
            case ETHEREUM:
                rawTransaction = RawTransaction.createEthereumTransaction(from, nonce, gasLimit, gasPrice, value, data, deadline, extra);
        }
        if (rawTransaction == null) {
            rawTransaction = RawTransaction.createTransaction(from, to, interpreter, nonce, gasLimit, gasPrice,
                    value, data, deadline, extra);
        }
        String ret;

        if (isEIP155) {
            ret = signEIP155(rawTransaction, credentials, chainId);
        } else {
            ret = sign(rawTransaction, credentials);
        }
        SignRes res = new SignRes();
        res.setRaw_transaction(ret);
        res.setTransaction_hash(Hash.sha3(ret));
        System.out.println(String.format("sign nonce:%s; gasPrice:%s; gasLimit:%s; to:%s; value:%s; funName:%s; txHash:%s; data:%s",
                nonce.toString(), gasPrice.toString(), gasLimit.toString(), to, value.toString(), "unknow", res.getTransaction_hash(), data));
        return res;
    }

    /**
     * 签名EIP155
     *
     * @param rawTransaction
     * @param credentials
     * @param chainId
     * @return
     */
    public static String signEIP155(RawTransaction rawTransaction, Credentials credentials, int chainId) {
        byte[] signedMessage;
        signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials);
        return Numeric.toHexString(signedMessage);
    }

    /**
     * 非EIP155的交易
     *
     * @param rawTransaction
     * @param credentials
     * @return
     */
    public static String sign(RawTransaction rawTransaction, Credentials credentials) {
        byte[] signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        return Numeric.toHexString(signedMessage);
    }

    //==================发送交易===============

    /**
     * 签名交易
     *
     * @param credentials     签名（签名不能为空）
     * @param to              收款地址（不能为空）
     * @param contractAddress 合约地址。（如果是以太坊自定义合约类型，可为空）
     * @param nonce           nonce值
     * @param gasPrice        单价（位）(为null则使用系统内部默认值)
     * @param gas             数量(为null则使用系统内部默认值)
     * @param value           转账金额
     * @return
     * @throws Exception
     */
//    public static SignRes signTransfer(boolean isEIP155, Credentials credentials,
//                                       String from, String to, Interpreter interpreter, String contractAddress, BigInteger nonce, BigInteger gasPrice,
//                                       BigInteger gas, BigInteger value, int chainId, Boolean hasToken) throws Exception {
//        if (StringUtils.isEmpty(to)) {
//            throw new Exception("the toAddress should not be null");
//        }
//        if (credentials == null) {
//            throw new Exception("the credentials should not be null");
//        }
//        SignRes signData;
//        BigInteger gasLimit;
//        if (StringUtils.isEmpty(contractAddress)) {
//            if (gasPrice == null) {
//                gasPrice = GAS_PRICE;
//            }
//            if (gas == null || gas.compareTo(BigInteger.ZERO) == 0) {
//                gasLimit = GAS_LIMIT;
//            } else {
//                gasLimit = gas;
//            }
//            signData = sign(isEIP155, credentials, from, to, interpreter, nonce, gasLimit, gasPrice,
//                    value, null, deadline, extra, chainId);
//        } else {
//            Address _to = new Address(to);
//            Uint256 _value = new Uint256(value);
//            Function function = new Function("transfer", Arrays.<Type>asList(_to, _value), Collections.<TypeReference<?>>emptyList());
//            String dataHex = FunctionEncoder.encode(function);
//            if (gasPrice == null) {
//                gasPrice = GAS_PRICE;
//            }
//            if (gas == null || gas.compareTo(BigInteger.ZERO) == 0) {
//                gasLimit = GAS_LIMIT;
//            } else {
//                gasLimit = gas;
//            }
//            //合约的valus一定只能是0
//            signData = sign(isEIP155, credentials, to, nonce, gasPrice, gas, BigInteger.ZERO, dataHex, chainId, hasToken);
//        }
//
//        return signData;
//    }


    /**
     * 发送交易
     *
     * @param tx
     * @return
     * @throws Exception
     */
    public static String sendTransaction(Chain5j chain5j, TxType txType, String tx) throws Exception {
        Request<?, SendTransaction> ethSendTransactionRequest = chain5j.sendRawTransaction(txType, tx);
        SendTransaction sendTransaction = ethSendTransactionRequest.send();
        if (sendTransaction.hasError()) {
            throw new Exception(sendTransaction.getError().toString());
        }
        String transactionHash = sendTransaction.getTransactionHash();
        if (sendTransaction.getError() != null) {
            throw new Exception(sendTransaction.getError().getMessage());
        }
        return transactionHash;
    }

    //================签名内容=============

    /**
     * 签名信息
     *
     * @param msg
     * @param ecKeyPair
     * @return
     */
    public static Sign.SignatureData signMsg(String msg, ECKeyPair ecKeyPair) {
        Sign.SignatureData signatureData = Sign.signMessage(msg.getBytes(), ecKeyPair);
        return signatureData;
    }

    /**
     * 恢复地址
     *
     * @param msg
     * @param signatureData
     * @return 返回地址
     */
    public static String recoverAddress(String msg, Sign.SignatureData signatureData) {
        return recoverAddress(null, msg, signatureData);
    }

    public static String recoverAddress(String icapPrefix, String msg, Sign.SignatureData signatureData) {
        BigInteger publick = null;
        try {
            publick = Sign.signedMessageToKey(msg.getBytes(), signatureData);
        } catch (SignatureException e) {
        }
        String address = Keys.getAddress(icapPrefix, publick);
        return Numeric.prependHexPrefix(address);
    }
}
