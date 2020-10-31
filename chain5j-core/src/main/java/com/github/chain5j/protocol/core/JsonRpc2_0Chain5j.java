package com.github.chain5j.protocol.core;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.ScheduledExecutorService;

import com.github.chain5j.protocol.core.methods.request.RpcFilter;
import com.github.chain5j.protocol.core.methods.request.Transaction;
import com.github.chain5j.protocol.core.methods.response.*;
import com.github.chain5j.protocol.websocket.events.NewHeadsNotification;
import com.github.chain5j.constant.TxType;
import com.github.chain5j.protocol.Chain5j;
import com.github.chain5j.protocol.Chain5jService;
import com.github.chain5j.protocol.core.methods.response.*;
import io.reactivex.Flowable;

import com.github.chain5j.protocol.core.methods.response.BlockHeight;
import com.github.chain5j.protocol.core.methods.response.GetBalance;
import com.github.chain5j.protocol.core.methods.response.RpcGetTransactionReceipt;
import com.github.chain5j.protocol.core.methods.response.RpcLog;
import com.github.chain5j.protocol.core.methods.response.RpcSubscribe;
import com.github.chain5j.protocol.core.methods.response.RpcTransaction;
import com.github.chain5j.protocol.core.methods.response.NetListening;
import com.github.chain5j.protocol.core.methods.response.NetVersion;
import com.github.chain5j.protocol.rx.JsonRpc2_0Rx;
import com.github.chain5j.utils.Async;
import com.github.chain5j.utils.Numeric;

public class JsonRpc2_0Chain5j implements Chain5j {

    public static final int DEFAULT_BLOCK_TIME = 15 * 1000;

    protected final Chain5jService chain5jService;
    private final JsonRpc2_0Rx rpcRx;
    private final long blockTime;
    private final ScheduledExecutorService scheduledExecutorService;

    public JsonRpc2_0Chain5j(Chain5jService chain5jService) {
        this(chain5jService, DEFAULT_BLOCK_TIME, Async.defaultExecutorService());
    }

    public JsonRpc2_0Chain5j(
            Chain5jService chain5jService, long pollingInterval,
            ScheduledExecutorService scheduledExecutorService) {
        this.chain5jService = chain5jService;
        this.rpcRx = new JsonRpc2_0Rx(this, scheduledExecutorService);
        this.blockTime = pollingInterval;
        this.scheduledExecutorService = scheduledExecutorService;
    }

    // =========Account=======
    @Override
    public Request<?, GetAccountInfo> accountInfo(String accountName, DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
                "accounts_accountInfo",
                Arrays.asList(accountName, defaultBlockParameter),
                chain5jService,
                GetAccountInfo.class);
    }

    // =========Apps=======
    @Override
    public Request<?, GetBalance> getBalance(
            String address, DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
                "apps_getBalance",
                Arrays.asList(address, defaultBlockParameter.getValue()),
                chain5jService,
                GetBalance.class);
    }

    @Override
    public Request<?, GetTransactionCount> getTransactionCount(
            String address, DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
                "apps_getTransactionCount",
                Arrays.asList(address, defaultBlockParameter.getValue()),
                chain5jService,
                GetTransactionCount.class);
    }

    @Override
    public Request<?, CallContract> callContract(Transaction transaction, DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
                "apps_call",
                Arrays.asList(transaction, defaultBlockParameter),
                chain5jService,
                CallContract.class);
    }
    // =========Chain5j=======

    @Override
    public Request<?, SendTransaction> sendTransaction(TxType txType, Transaction transaction) {
        return new Request<>(
                "chain5j_sendTransaction",
                Arrays.asList(txType.getValue(), transaction),
                chain5jService,
                SendTransaction.class);
    }

    @Override
    public Request<?, SendTransaction> sendRawTransaction(TxType txType, String signedTransactionData) {
        return new Request<>(
                "chain5j_sendRawTransaction",
                Arrays.asList(txType.getValue(), signedTransactionData),
                chain5jService,
                SendTransaction.class);
    }

    @Override
    public Request<?, RpcTransaction> getTransactionByHash(String transactionHash) {
        return new Request<>(
                "chain5j_getTransaction",
                Arrays.asList(transactionHash),
                chain5jService,
                RpcTransaction.class);
    }

    @Override
    public Request<?, RpcGetTransactionReceipt> getTransactionReceipt(String transactionHash) {
        return new Request<>(
                "chain5j_getTransactionReceipt",
                Arrays.asList(transactionHash),
                chain5jService,
                RpcGetTransactionReceipt.class);
    }

    @Override
    public Request<?, RpcLog> rpcGetFilterLogs(BigInteger filterId) {
        return new Request<>(
                "chain5j_getFilterLogs",
                Arrays.asList(Numeric.toHexStringWithPrefixSafe(filterId)),
                chain5jService,
                RpcLog.class);
    }

    @Override
    public Request<?, RpcLog> rpcGetLogs(
            RpcFilter rpcFilter) {
        return new Request<>(
                "chain5j_getLogs",
                Arrays.asList(rpcFilter),
                chain5jService,
                RpcLog.class);
    }

    @Override
    public Request<?, BlockHeight> blockHeight() {
        return new Request<>(
                "chain5j_blockHeight",
                Collections.<String>emptyList(),
                chain5jService,
                BlockHeight.class);
    }

    @Override
    public Request<?, BlockInfo> getBlockByHash(
            String blockHash) {
        return new Request<>(
                "chain5j_getBlockByHash",
                Arrays.asList(
                        blockHash
                ),
                chain5jService,
                BlockInfo.class);
    }

    @Override
    public Request<?, BlockInfo> getBlockByNumber(
            DefaultBlockParameter defaultBlockParameter) {
        return new Request<>(
                "chain5j_getBlockByNumber",
                Arrays.asList(
                        defaultBlockParameter.getValue()),
                chain5jService,
                BlockInfo.class);
    }

    @Override
    public Flowable<NewHeadsNotification> newHeadsNotifications() {
        return chain5jService.subscribe(
                new Request<>(
                        "chain5j_subscribe",
                        Collections.singletonList("newHeads"),
                        chain5jService,
                        RpcSubscribe.class),
                "chain5j_unsubscribe",
                NewHeadsNotification.class
        );
    }


    @Override
    public Request<?, NetVersion> netVersion() {
        return new Request<>(
                "net_version",
                Collections.<String>emptyList(),
                chain5jService,
                NetVersion.class);
    }

    @Override
    public Request<?, NetListening> netListening() {
        return new Request<>(
                "net_listening",
                Collections.<String>emptyList(),
                chain5jService,
                NetListening.class);
    }

    @Override
    public Request<?, NetPeerCount> netPeerCount() {
        return new Request<>(
                "net_peerCount",
                Collections.<String>emptyList(),
                chain5jService,
                NetPeerCount.class);
    }

    @Override
    public void shutdown() {
        scheduledExecutorService.shutdown();
        try {
            chain5jService.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to close web3j service", e);
        }
    }
}
