package com.github.chain5j.tx;

import java.io.IOException;
import java.math.BigInteger;

import com.github.chain5j.protocol.Chain5j;
import com.github.chain5j.tx.response.TransactionReceiptProcessor;
import com.github.chain5j.crypto.Credentials;

/**
 * Simple RawTransactionManager derivative that manages nonces to facilitate multiple transactions
 * per block.
 */
public class FastRawTransactionManager extends RawTransactionManager {

    private volatile BigInteger nonce = BigInteger.valueOf(-1);

    public FastRawTransactionManager(Chain5j chain5j, Credentials credentials, byte chainId) {
        super(chain5j, credentials, chainId);
    }

    public FastRawTransactionManager(Chain5j chain5j, Credentials credentials) {
        super(chain5j, credentials);
    }

    public FastRawTransactionManager(
            Chain5j chain5j, Credentials credentials,
            TransactionReceiptProcessor transactionReceiptProcessor) {
        super(chain5j, credentials, ChainId.NONE, transactionReceiptProcessor);
    }

    public FastRawTransactionManager(
            Chain5j chain5j, Credentials credentials, byte chainId,
            TransactionReceiptProcessor transactionReceiptProcessor) {
        super(chain5j, credentials, chainId, transactionReceiptProcessor);
    }

    @Override
    protected synchronized BigInteger getNonce() throws IOException {
        if (nonce.signum() == -1) {
            // obtain lock
            nonce = super.getNonce();
        } else {
            nonce = nonce.add(BigInteger.ONE);
        }
        return nonce;
    }

    public BigInteger getCurrentNonce() {
        return nonce;
    }

    public synchronized void resetNonce() throws IOException {
        nonce = super.getNonce();
    }

    public synchronized void setNonce(BigInteger value) {
        nonce = value;
    }
}
