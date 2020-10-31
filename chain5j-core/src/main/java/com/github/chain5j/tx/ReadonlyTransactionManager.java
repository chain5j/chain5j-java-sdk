package com.github.chain5j.tx;

import java.io.IOException;
import java.math.BigInteger;

import com.github.chain5j.protocol.Chain5j;
import com.github.chain5j.protocol.core.methods.response.SendTransaction;
import com.github.chain5j.crypto.Interpreter;

/**
 * Transaction manager implementation for read-only operations on smart contracts.
 */
public class ReadonlyTransactionManager extends TransactionManager {

    public ReadonlyTransactionManager(Chain5j chain5j, String fromAddress) {
        super(chain5j, fromAddress);
    }

    @Override
    public SendTransaction sendTransaction(
            String from, String to, Interpreter interpreter, BigInteger gas, BigInteger gasPrice,
            BigInteger value, String data, BigInteger deadline, String extra)
            throws IOException {
        throw new UnsupportedOperationException(
                "Only read operations are supported by this transaction manager");
    }
}
