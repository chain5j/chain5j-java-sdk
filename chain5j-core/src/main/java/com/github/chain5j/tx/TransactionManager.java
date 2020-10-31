package com.github.chain5j.tx;

import java.io.IOException;
import java.math.BigInteger;

import com.github.chain5j.protocol.Chain5j;
import com.github.chain5j.protocol.core.JsonRpc2_0Chain5j;
import com.github.chain5j.protocol.core.methods.response.SendTransaction;
import com.github.chain5j.protocol.core.methods.response.TransactionReceipt;
import com.github.chain5j.protocol.exceptions.TransactionException;
import com.github.chain5j.tx.response.PollingTransactionReceiptProcessor;
import com.github.chain5j.tx.response.TransactionReceiptProcessor;
import com.github.chain5j.crypto.Interpreter;

/**
 * Transaction manager abstraction for executing transactions with Ethereum client via
 * various mechanisms.
 */
public abstract class TransactionManager {

    public static final int DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH = 40;
    public static final long DEFAULT_POLLING_FREQUENCY = JsonRpc2_0Chain5j.DEFAULT_BLOCK_TIME;

    private final TransactionReceiptProcessor transactionReceiptProcessor;
    private final String fromAddress;

    protected TransactionManager(
            TransactionReceiptProcessor transactionReceiptProcessor, String fromAddress) {
        this.transactionReceiptProcessor = transactionReceiptProcessor;
        this.fromAddress = fromAddress;
    }

    protected TransactionManager(Chain5j chain5j, String fromAddress) {
        this(new PollingTransactionReceiptProcessor(
                        chain5j, DEFAULT_POLLING_FREQUENCY, DEFAULT_POLLING_ATTEMPTS_PER_TX_HASH),
                fromAddress);
    }

    protected TransactionManager(
            Chain5j chain5j, int attempts, long sleepDuration, String fromAddress) {
        this(new PollingTransactionReceiptProcessor(chain5j, sleepDuration, attempts), fromAddress);
    }

    protected TransactionReceipt executeTransaction(
            String from, String to, Interpreter interpreter, BigInteger gas, BigInteger gasPrice,
            BigInteger value,String data)
            throws IOException, TransactionException {

        SendTransaction sendTransaction = sendTransaction(
                from, to, interpreter, gas, gasPrice,
                value, data, BigInteger.ZERO, null);
        return processResponse(sendTransaction);
    }

    public abstract SendTransaction sendTransaction(
            String from, String to, Interpreter interpreter, BigInteger gas, BigInteger gasPrice,
            BigInteger value, String data, BigInteger deadline, String extra)
            throws IOException;

    public String getFromAddress() {
        return fromAddress;
    }

    private TransactionReceipt processResponse(SendTransaction transactionResponse)
            throws IOException, TransactionException {
        if (transactionResponse.hasError()) {
            throw new RuntimeException("Error processing transaction request: "
                    + transactionResponse.getError().getMessage());
        }

        String transactionHash = transactionResponse.getTransactionHash();

        return transactionReceiptProcessor.waitForTransactionReceipt(transactionHash);
    }


}
