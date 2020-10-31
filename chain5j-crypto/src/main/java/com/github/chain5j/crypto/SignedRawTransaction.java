package com.github.chain5j.crypto;

import java.math.BigInteger;

public class SignedRawTransaction extends RawTransaction {

    private static final int CHAIN_ID_INC = 35;
    private static final int LOWER_REAL_V = 27;

    private Sign.SignatureData signatureData;

    public SignedRawTransaction(String from, String to, Interpreter interpreter, BigInteger nonce, BigInteger gasLimit, BigInteger gasPrice,
                                BigInteger value, String data, BigInteger deadline, String extra,
                                Sign.SignatureData signatureData) {
        super(from, to, interpreter, nonce, gasLimit, gasPrice,
                value, data, deadline, extra);
        this.signatureData = signatureData;
    }

    public Sign.SignatureData getSignatureData() {
        return signatureData;
    }

//    public String getFrom() throws SignatureException {
//        Integer chainId = getChainId();
//        byte[] encodedTransaction;
//        if (null == chainId) {
//            encodedTransaction = TransactionEncoder.encode(this);
//        } else {
//            encodedTransaction = TransactionEncoder.encode(this, chainId.byteValue());
//        }
//        int v = signatureData.getV();
//        byte[] r = signatureData.getR();
//        byte[] s = signatureData.getS();
//        Sign.SignatureData signatureDataV = new Sign.SignatureData(getRealV(v), r, s);
//        BigInteger key = Sign.signedMessageToKey(encodedTransaction, signatureDataV);
//        if (StringUtils.isNotEmpty(icapPrefix)) {
//            return Keys.getAddress(icapPrefix, key);
//        }
//        return "0x" + Keys.getAddress(icapPrefix, key);
//    }

//    public void verify(String from) throws SignatureException {
//        String actualFrom = getFrom();
//        if (!actualFrom.equals(from)) {
//            throw new SignatureException("from mismatch");
//        }
//    }

    private int getRealV(int v) {
        if (v == LOWER_REAL_V || v == (LOWER_REAL_V + 1)) {
            return v;
        }
        int realV = LOWER_REAL_V;
        int inc = 0;
        if (v % 2 == 0) {
            inc = 1;
        }
        return realV + inc;
    }

    public Integer getChainId() {
        int v = signatureData.getV();
        if (v == LOWER_REAL_V || v == (LOWER_REAL_V + 1)) {
            return null;
        }
        Integer chainId = (v - CHAIN_ID_INC) / 2;
        return chainId;
    }
}
