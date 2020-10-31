package com.github.chain5j.protocol.core.methods.response;

import com.google.gson.Gson;

public class Transaction {
    private static final int CHAIN_ID_INC = 35;
    private static final int LOWER_REAL_V = 27;

    /**
     * {
     *     "blockHash":"0xe62cc6a7428a8e08c406f34ddc1d10768b05afc0bf222f439d63f859c8ff8b0e",
     *     "blockNumber":"0x6",
     *     "transactionIndex":"0x0",
     *     "type":0,
     *     "transaction":{
     *         "from":"user1@chain5j.com",
     *         "to":"admin@dev.chain5j.com",
     *         "interpreter":"chain5j.base",
     *         "nonce":1,
     *         "gasLimit":30000,
     *         "gasPrice":0,
     *         "value":"0x16",
     *         "input":"",
     *         "deadline":0,
     *         "signature":"0xf849845032353680b84108cffd38f895862ff24337a8973587401b3f7ad70e2c2f1bdf71aed9e03c6fbf15685a0214a83967230a6a2bbf00502c823fc7193123456c79250e966374210d01",
     *         "extra":"",
     *         "hash":"0xe4fc041bded0d0f6ace5a1d7a13789c01110c30232ba13b1c0b57a1d11423f6a"
     *     }
     * }
     */

    private String blockHash;
    private String blockNumber;
    private String transactionIndex;
    private String type;
    private TransactionObject transaction;

    public Transaction() {
    }


    public String getBlockHash() {
        return blockHash;
    }

    public void setBlockHash(String blockHash) {
        this.blockHash = blockHash;
    }

    public String getBlockNumber() {
        return blockNumber;
    }

    public void setBlockNumber(String blockNumber) {
        this.blockNumber = blockNumber;
    }

    public String getTransactionIndex() {
        return transactionIndex;
    }

    public void setTransactionIndex(String transactionIndex) {
        this.transactionIndex = transactionIndex;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public TransactionObject getTransaction() {
        return transaction;
    }

    public void setTransaction(TransactionObject transaction) {
        this.transaction = transaction;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
