package com.github.chain5j.tx.response;

import java.io.IOException;
import java.util.Optional;

import com.github.chain5j.protocol.Chain5j;
import com.github.chain5j.protocol.core.methods.response.RpcGetTransactionReceipt;
import com.github.chain5j.protocol.core.methods.response.TransactionReceipt;
import com.github.chain5j.protocol.exceptions.TransactionException;

/**
 * Abstraction for managing how we wait for transaction receipts to be generated on the network.
 */
public abstract class TransactionReceiptProcessor {

    private final Chain5j chain5j;

    public TransactionReceiptProcessor(Chain5j chain5j) {
        this.chain5j = chain5j;
    }

    public abstract TransactionReceipt waitForTransactionReceipt(
            String transactionHash)
            throws IOException, TransactionException;

    Optional<TransactionReceipt> sendTransactionReceiptRequest(
            String transactionHash) throws IOException, TransactionException {
        RpcGetTransactionReceipt transactionReceipt =
                chain5j.getTransactionReceipt(transactionHash).send();
        if (transactionReceipt.hasError()) {
            throw new TransactionException("Error processing request: "
                    + transactionReceipt.getError().getMessage());
        }

        return transactionReceipt.getTransactionReceipt();
    }
}
