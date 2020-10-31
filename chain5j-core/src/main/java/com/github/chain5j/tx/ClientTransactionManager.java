package com.github.chain5j.tx;

import java.io.IOException;
import java.math.BigInteger;

import com.github.chain5j.constant.TxType;
import com.github.chain5j.protocol.Chain5j;
import com.github.chain5j.protocol.core.methods.request.Transaction;
import com.github.chain5j.protocol.core.methods.response.SendTransaction;
import com.github.chain5j.tx.response.TransactionReceiptProcessor;
import com.github.chain5j.crypto.Interpreter;

/**
 * TransactionManager implementation for using an Ethereum node to transact.
 *
 * <p><b>Note</b>: accounts must be unlocked on the node for transactions to be successful.
 */
public class ClientTransactionManager extends TransactionManager {

    private final Chain5j chain5j;

    public ClientTransactionManager(
            Chain5j chain5j, String fromAddress) {
        super(chain5j, fromAddress);
        this.chain5j = chain5j;
    }

    public ClientTransactionManager(
            Chain5j chain5j, String fromAddress, int attempts, int sleepDuration) {
        super(chain5j, attempts, sleepDuration, fromAddress);
        this.chain5j = chain5j;
    }

    public ClientTransactionManager(
            Chain5j chain5j, String fromAddress,
            TransactionReceiptProcessor transactionReceiptProcessor) {
        super(transactionReceiptProcessor, fromAddress);
        this.chain5j = chain5j;
    }

    @Override
    public SendTransaction sendTransaction(
            String from, String to, Interpreter interpreter, BigInteger gas, BigInteger gasPrice,
            BigInteger value, String data, BigInteger deadline, String extra)
            throws IOException {

        Transaction transaction = Transaction.createTransaction(from, to, interpreter, null, gas, gasPrice,
                value, data, deadline, extra);

        return chain5j.sendTransaction(TxType.STATE, transaction)
                .send();
    }
}
