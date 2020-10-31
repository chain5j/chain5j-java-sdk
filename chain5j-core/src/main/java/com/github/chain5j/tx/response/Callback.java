package com.github.chain5j.tx.response;

import com.github.chain5j.protocol.core.methods.response.TransactionReceipt;

/**
 * Transaction receipt processor callback.
 */
public interface Callback {
    void accept(TransactionReceipt transactionReceipt);

    void exception(Exception exception);
}
