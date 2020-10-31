package com.github.chain5j.tx.response;

import java.io.IOException;

import com.github.chain5j.protocol.Chain5j;
import com.github.chain5j.protocol.core.methods.response.TransactionReceipt;
import com.github.chain5j.protocol.exceptions.TransactionException;

/**
 * Return an {@link EmptyTransactionReceipt} receipt back to callers containing only the
 * transaction hash.
 */
public class NoOpProcessor extends TransactionReceiptProcessor {

    public NoOpProcessor(Chain5j chain5j) {
        super(chain5j);
    }

    @Override
    public TransactionReceipt waitForTransactionReceipt(String transactionHash)
            throws IOException, TransactionException {
        return new EmptyTransactionReceipt(transactionHash);
    }
}
