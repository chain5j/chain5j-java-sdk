package com.github.chain5j.tx;

import java.io.IOException;
import java.math.BigInteger;

import com.github.chain5j.constant.TxType;
import com.github.chain5j.crypto.*;
import com.github.chain5j.protocol.Chain5j;
import com.github.chain5j.protocol.core.DefaultBlockParameterName;
import com.github.chain5j.protocol.core.methods.response.GetTransactionCount;
import com.github.chain5j.protocol.core.methods.response.SendTransaction;
import com.github.chain5j.tx.exceptions.TxHashMismatchException;
import com.github.chain5j.tx.response.TransactionReceiptProcessor;
import com.github.chain5j.utils.TxHashVerifier;
import com.github.chain5j.crypto.*;
import com.github.chain5j.utils.Numeric;

public class RawTransactionManager extends TransactionManager {

    private final Chain5j chain5j;
    final Credentials credentials;

    private final byte chainId;

    protected TxHashVerifier txHashVerifier = new TxHashVerifier();

    public RawTransactionManager(Chain5j chain5j, Credentials credentials, byte chainId) {
        super(chain5j, credentials.getAddress());

        this.chain5j = chain5j;
        this.credentials = credentials;

        this.chainId = chainId;
    }

    public RawTransactionManager(
            Chain5j chain5j, Credentials credentials, byte chainId,
            TransactionReceiptProcessor transactionReceiptProcessor) {
        super(transactionReceiptProcessor, credentials.getAddress());

        this.chain5j = chain5j;
        this.credentials = credentials;

        this.chainId = chainId;
    }

    public RawTransactionManager(
            Chain5j chain5j, Credentials credentials, byte chainId, int attempts, long sleepDuration) {
        super(chain5j, attempts, sleepDuration, credentials.getAddress());

        this.chain5j = chain5j;
        this.credentials = credentials;

        this.chainId = chainId;
    }

    public RawTransactionManager(Chain5j chain5j, Credentials credentials) {
        this(chain5j, credentials, ChainId.NONE);
    }

    public RawTransactionManager(
            Chain5j chain5j, Credentials credentials, int attempts, int sleepDuration) {
        this(chain5j, credentials, ChainId.NONE, attempts, sleepDuration);
    }

    protected BigInteger getNonce() throws IOException {
        GetTransactionCount ethGetTransactionCount = chain5j.getTransactionCount(
                credentials.getAddress(), DefaultBlockParameterName.PENDING).send();

        return ethGetTransactionCount.getTransactionCount();
    }

    public TxHashVerifier getTxHashVerifier() {
        return txHashVerifier;
    }

    public void setTxHashVerifier(TxHashVerifier txHashVerifier) {
        this.txHashVerifier = txHashVerifier;
    }

    @Override
    public SendTransaction sendTransaction(
            String from, String to, Interpreter interpreter, BigInteger gas, BigInteger gasPrice,
            BigInteger value, String data, BigInteger deadline, String extra) throws IOException {

        BigInteger nonce = getNonce();

        RawTransaction rawTransaction = RawTransaction.createTransaction(
                from, to, interpreter, nonce, gas, gasPrice,
                value, data, deadline, extra);

        return signAndSend(rawTransaction);
    }

    /*
     * @param rawTransaction a RawTransaction istance to be signed
     * @return The transaction signed and encoded without ever broadcasting it
     */
    public String sign(RawTransaction rawTransaction) {

        byte[] signedMessage;

        if (chainId > ChainId.NONE) {
            signedMessage = TransactionEncoder.signMessage(rawTransaction, chainId, credentials);
        } else {
            signedMessage = TransactionEncoder.signMessage(rawTransaction, credentials);
        }

        return Numeric.toHexString(signedMessage);
    }

    public SendTransaction signAndSend(RawTransaction rawTransaction)
            throws IOException {
        String hexValue = sign(rawTransaction);
        SendTransaction sendTransaction = chain5j.sendRawTransaction(TxType.STATE, hexValue).send();

        if (sendTransaction != null && !sendTransaction.hasError()) {
            String txHashLocal = Hash.sha3(hexValue);
            String txHashRemote = sendTransaction.getTransactionHash();
            if (!txHashVerifier.verify(txHashLocal, txHashRemote)) {
                throw new TxHashMismatchException(txHashLocal, txHashRemote);
            }
        }

        return sendTransaction;
    }
}
