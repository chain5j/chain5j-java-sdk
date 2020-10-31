package com.github.chain5j.protocol.core;

import java.math.BigInteger;

import com.github.chain5j.protocol.core.methods.request.RpcFilter;
import com.github.chain5j.protocol.core.methods.request.Transaction;
import com.github.chain5j.protocol.core.methods.response.*;
import com.github.chain5j.constant.TxType;
import com.github.chain5j.protocol.core.methods.response.*;
import com.github.chain5j.protocol.core.methods.response.GetBalance;
import com.github.chain5j.protocol.core.methods.response.RpcGetTransactionReceipt;
import com.github.chain5j.protocol.core.methods.response.RpcLog;
import com.github.chain5j.protocol.core.methods.response.RpcTransaction;
import com.github.chain5j.protocol.core.methods.response.NetListening;
import com.github.chain5j.protocol.core.methods.response.NetVersion;

public interface Pangu {
    // =========Account=======
    Request<?, GetAccountInfo> accountInfo(
            String accountName, DefaultBlockParameter defaultBlockParameter);
//    Request<?, PartnerData> partner(
//            String accountName, DefaultBlockParameter defaultBlockParameter);
//    Request<?, DomainStore> domainInfo(
//            String domain, DefaultBlockParameter defaultBlockParameter);

    // =========Apps=======
    Request<?, GetBalance> getBalance(
            String address, DefaultBlockParameter defaultBlockParameter);


    Request<?, GetTransactionCount> getTransactionCount(
            String address, DefaultBlockParameter defaultBlockParameter);

    Request<?, CallContract> callContract(
            Transaction transaction,
            DefaultBlockParameter defaultBlockParameter);

    // =========Chain5j=======
    Request<?, SendTransaction> sendTransaction(TxType txType, Transaction transaction);

    Request<?, SendTransaction> sendRawTransaction(TxType txType, String signedTransactionData);


    Request<?, RpcTransaction> getTransactionByHash(String transactionHash);

    Request<?, RpcGetTransactionReceipt> getTransactionReceipt(String transactionHash);
//    Request<?, Receipts> ReceiptsInBlock(DefaultBlockParameter defaultBlockParameter);
//    Request<?, Log> GetTransactionLogs(DefaultBlockParameter defaultBlockParameter);

    Request<?, RpcLog> rpcGetLogs(RpcFilter rpcFilter);

    Request<?, RpcLog> rpcGetFilterLogs(BigInteger filterId);


    Request<?, BlockHeight> blockHeight();

    Request<?, BlockInfo> getBlockByHash(String blockHash);

    Request<?, BlockInfo> getBlockByNumber(
            DefaultBlockParameter defaultBlockParameter);

    Request<?, NetVersion> netVersion();

    Request<?, NetListening> netListening();

    Request<?, NetPeerCount> netPeerCount();
}
