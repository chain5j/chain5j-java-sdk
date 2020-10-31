package com.github.chain5j.engine.sync;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.chain5j.engine.sync.vo.ChainBlockInfo;
import com.github.chain5j.engine.sync.vo.ChainTransactionInfo;
import com.github.chain5j.engine.sync.vo.ChainTransactionReceiptInfo;
import com.github.chain5j.protocol.Chain5j;
import com.github.chain5j.protocol.core.DefaultBlockParameter;
import com.github.chain5j.protocol.core.Request;
import com.github.chain5j.protocol.core.methods.response.*;
import com.google.gson.Gson;
import com.github.chain5j.protocol.core.methods.response.*;
import com.github.chain5j.utils.json.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2019-06-03 19:29
 * @Copyright Copyright@2019
 */
public class BlockSyncEngine {
    static Logger log = LoggerFactory.getLogger(BlockSyncEngine.class);
    private static final Long WAITING_TIME = 60000L;
    static BlockSyncEngine ins;
    private static Chain5j chain5j;
    private static BlockSyncCallback callback;


    private BlockSyncEngine() {
    }

    private BlockSyncEngine(Chain5j chain5j, BlockSyncCallback callback) {
        if (ins == null) {
            BlockSyncEngine.chain5j = chain5j;
            BlockSyncEngine.callback = callback;
        }
    }

    public static BlockSyncEngine bulid(Chain5j chain5j, BlockSyncCallback callback) {
        if (ins == null) {
            ins = new BlockSyncEngine(chain5j, callback);
        }
        return ins;
    }

    public void syncBlock(BigInteger currentBlockNumber) {
        getProtocolVersion(chain5j);
        if (currentBlockNumber.compareTo(BigInteger.ZERO) < 0) {
            currentBlockNumber = BigInteger.ZERO;
        }

        BigInteger lastBlockNumber = BigInteger.ZERO;
        while (true) {
            try {
                //获取链上最新的
                Request<?, BlockHeight> ethBlockNumberRequest = chain5j.blockHeight();
                lastBlockNumber = ethBlockNumberRequest.send().blockHeight();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                waitTime();
                syncBlock(currentBlockNumber);
                return;
            }
            if (currentBlockNumber.compareTo(lastBlockNumber) > 0) {
                // 当前线程等待1分钟
                waitTime();
                continue;
            }
            try {
                currentBlockNumber = queryBlockCurrentBlockNumber(currentBlockNumber, lastBlockNumber);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                waitTime();
                syncBlock(currentBlockNumber);
                return;
            }
        }
    }

    public void waitTime() {
        try {
            Thread.sleep(WAITING_TIME);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * Description: 查询协议，检测是否能够连接
     * </p>
     *
     * @param
     * @return void
     * @Author: xwc1125
     * @Date: 2019-05-09 10:29:49
     */
    private void getProtocolVersion(Chain5j chain5j) {
//        Request<?, RpcProtocolVersion> protocolVersionRequest = chain5j.ethProtocolVersion();
//        try {
//            String protocolVersion = protocolVersionRequest.send().getProtocolVersion();
//            System.out.println("protocolVersion:" + protocolVersion);
//        } catch (IOException e) {
//            log.error("无法连接");
//            log.error(e.getMessage(), e);
//            waitTime();
//            getProtocolVersion(chain5j);
//            return;
//        }
    }

    /**
     * 查询当前和最新块的信息
     *
     * @param currentBlockNumber 当前块
     * @param lastBlockNumber    最新链上块
     */
    private BigInteger queryBlockCurrentBlockNumber(BigInteger currentBlockNumber, BigInteger lastBlockNumber) throws IOException {
        while (true) {
            if (currentBlockNumber.compareTo(lastBlockNumber) > 0) {
                return currentBlockNumber;
            }
            queryBlock(currentBlockNumber);
            //加1
            currentBlockNumber = currentBlockNumber.add(BigInteger.ONE);
        }
    }

    /**
     * 查询区块方法,方法中校验是否分叉
     *
     * @param currentBlockNumber
     * @return
     * @throws IOException
     */
    private void queryBlock(BigInteger currentBlockNumber) throws IOException {
        // 读取当前链上blockNumber为i的区块
        Request<?, BlockInfo> ethGetBlockByNumber = chain5j.getBlockByNumber(DefaultBlockParameter.valueOf(currentBlockNumber));
        BlockInfo.Block block = ethGetBlockByNumber.send().getBlock();
        //获取父类的hash
        String parentHash = block.getParentHash();

        // 从数据库中查询数据库中持久化的上一区块hash
        ChainBlockInfo one = null;
        if (callback != null) {
            one = callback.findBlockByNumber(currentBlockNumber.subtract(BigInteger.ONE));
        }
        if (one == null) {
            getBlockInfo(block);
            return;
        }
        String hash = one.getHash();
        //当前块是数据库块中的hash，回退12个块
        if (parentHash.equals(hash)) {
            getBlockInfo(block);
            return;
        } else {
            //出现分叉
            currentBlockNumber = currentBlockNumber.subtract(BigInteger.ONE);
            // 调用数据库中的方法删除上一区块的交易信息和另一个表中的区块信息
            // 删除交易信息
            if (callback != null) {
                callback.deleteTransactionByBlockHash(hash);
                // 删除block表中的分叉区块的数据
                callback.deleteBlockByNumber(currentBlockNumber);
            }
            return;
        }
    }

    /**
     * 保存区块链上的块信息
     *
     * @param block
     */
    void getBlockInfo(BlockInfo.Block block) {
        ChainBlockInfo blockInfo = null;
        try {
            blockInfo = JSON.getObjectMapper().readValue(block.toJsonString(), ChainBlockInfo.class);
        } catch (JsonProcessingException e) {
            if (callback != null) {
                callback.logException(e);
            }
        } catch (IOException e) {
            if (callback != null) {
                callback.logException(e);
            }
        }
        if (blockInfo != null) {
            blockInfo.setStatus(StatusType.OK.value);
            List<ChainTransactionInfo> transactionInfoList = getTransactionInfo(block, block.getTransactions(), block.getTimestamp());
            blockInfo.setTransactionInfos(transactionInfoList);
            if (callback != null) {
                callback.saveBlock(blockInfo);
            }
        }
    }

    /**
     * 保存交易信息
     *
     * @param block
     * @param transactionResultList
     * @param timestamp
     */
    List<ChainTransactionInfo> getTransactionInfo(BlockInfo.Block block, List<TransactionObject> transactionResultList, BigInteger timestamp) {
        List<ChainTransactionInfo> transactionInfoList = new ArrayList<>();
        if (transactionResultList != null && transactionResultList.size() > 0) {
            ChainTransactionInfo transactionInfo;
            for (int i = 0; i < transactionResultList.size(); i++) {
                try {
                    TransactionObject transactionObject = transactionResultList.get(i);
                    Transaction transaction = new Transaction();
                    transaction.setBlockHash(block.getHash());
                    transaction.setBlockNumber(block.getHeightRaw());
                    transaction.setTransactionIndex(i + "");
//                    transaction.setType();
                    transaction.setTransaction(transactionObject);
                    transactionInfo = getTransaction(transaction, timestamp);
                    transactionInfoList.add(transactionInfo);

                } catch (JsonProcessingException e) {
                    if (callback != null) {
                        callback.logException(e);
                    }
                } catch (IOException e) {
                    if (callback != null) {
                        callback.logException(e);
                    }
                }
            }
        }
        return transactionInfoList;
    }

    ChainTransactionInfo getTransaction(Transaction transaction, BigInteger timestamp) throws IOException {
        Gson gson = new Gson();
        ChainTransactionInfo transactionInfo = gson.fromJson(transaction.toString(), ChainTransactionInfo.class);
//        ChainTransactionInfo transactionInfo = JSON.getObjectMapper().readValue(transaction.toString(), ChainTransactionInfo.class);
        transactionInfo.setTimestamp(timestamp.longValue());
        transactionInfo.setIsSuccess(StatusType.UNKNOWN.value);
        transactionInfo.setStatus(StatusType.OK.value);
        ChainTransactionReceiptInfo transactionReceipt = getTransactionReceiptInfo(transactionInfo.getTransaction().getHash());
        if (transactionReceipt != null) {
            transactionInfo.setTransactionReceiptInfo(transactionReceipt);
        }
        return transactionInfo;
    }


    ChainTransactionReceiptInfo getTransactionReceiptInfo(String txid) {
        ChainTransactionReceiptInfo transactionReceiptInfo = null;
        try {
            Request<?, RpcGetTransactionReceipt> ethGetTransactionReceiptRequest = chain5j.getTransactionReceipt(txid);
            RpcGetTransactionReceipt rpcGetTransactionReceipt = ethGetTransactionReceiptRequest.send();
            if (rpcGetTransactionReceipt.hasError()) {
                log.error(rpcGetTransactionReceipt.getError().toString());
                if (callback != null) {
                    callback.logException(new Exception(rpcGetTransactionReceipt.getError().toString()));
                }
                return null;
            }
            TransactionReceipt transactionReceipt = rpcGetTransactionReceipt.getTransactionReceipt().get();
            if (transactionReceipt != null) {
                transactionReceiptInfo = JSON.getObjectMapper().readValue(transactionReceipt.toString(), ChainTransactionReceiptInfo.class);
            }
        } catch (Exception e) {
            if (callback != null) {
                callback.logException(e);
            }
        }
        return transactionReceiptInfo;
    }

}
