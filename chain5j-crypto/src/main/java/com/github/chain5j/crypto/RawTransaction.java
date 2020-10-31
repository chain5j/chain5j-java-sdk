package com.github.chain5j.crypto;

import java.math.BigInteger;

import com.github.chain5j.utils.Numeric;

public class RawTransaction {

    private String from;
    private String to;
    private Interpreter interpreter;
    private BigInteger nonce;
    private BigInteger gasLimit;
    private BigInteger gasPrice;
    private BigInteger value;
    private String data;
    private BigInteger deadline = BigInteger.ZERO;
    private String extra;

    public RawTransaction(String from, String to, Interpreter interpreter, BigInteger nonce, BigInteger gasLimit, BigInteger gasPrice,
                          BigInteger value, String data, BigInteger deadline, String extra) {
        this.from = from;
        this.to = to;
        this.interpreter = interpreter;
        this.nonce = nonce;
        this.gasLimit = gasLimit;
        this.gasPrice = gasPrice;
        this.value = value;
        if (data != null) {
            this.data = Numeric.prependHexPrefix(data);
        }
        this.deadline = deadline;
        this.extra = extra;
    }

    public static RawTransaction createTransaction(String from, String to, Interpreter interpreter, BigInteger nonce, BigInteger gasLimit, BigInteger gasPrice,
                                                   BigInteger value, String data, BigInteger deadline, String extra) {
        return new RawTransaction(from, to, interpreter, nonce, gasLimit, gasPrice,
                value, data, deadline, extra);
    }

    public static RawTransaction createBaseTransaction(String from, String to, BigInteger nonce, BigInteger gasLimit, BigInteger gasPrice,
                                                       BigInteger value, String data,  BigInteger deadline, String extra) {
        return new RawTransaction(from, to, Interpreter.BASE, nonce, gasLimit, gasPrice,
                value, data, deadline, extra);
    }

    public static RawTransaction createAccountTransaction(String from, String to, BigInteger nonce,
                                                          String data, BigInteger deadline, String extra) {
        return new RawTransaction(from, to, Interpreter.ACCOUNT, nonce, BigInteger.valueOf(30000), null,
                null, data, deadline, extra);
    }

    public static RawTransaction createLostTransaction(String from, String to, BigInteger nonce,
                                                       String data, BigInteger deadline, String extra) {
        return new RawTransaction(from, to, Interpreter.LOST, nonce, null, null,
                null, data, deadline, extra);
    }

    public static RawTransaction createEvmTransaction(String from, BigInteger nonce, BigInteger gasLimit, BigInteger gasPrice,
                                                      BigInteger value, String data, BigInteger deadline, String extra) {
        return new RawTransaction(from, "", Interpreter.EVM, nonce, gasLimit, gasPrice,
                value, data, deadline, extra);
    }

    public static RawTransaction createCaTransaction(String from, String to, BigInteger nonce, BigInteger gasLimit, BigInteger gasPrice,
                                                     BigInteger value, String data, BigInteger deadline, String extra) {
        return new RawTransaction(from, to, Interpreter.CA, nonce, gasLimit, gasPrice,
                value, data, deadline, extra);
    }

    public static RawTransaction createPoeTransaction(String from, String to, BigInteger nonce, BigInteger gasLimit, BigInteger gasPrice,
                                                      BigInteger value, String data, BigInteger deadline, String extra) {
        return new RawTransaction(from, to, Interpreter.POE, nonce, gasLimit, gasPrice,
                value, data, deadline, extra);
    }

    public static RawTransaction createEthereumTransaction(String from, BigInteger nonce, BigInteger gasLimit, BigInteger gasPrice,
                                                           BigInteger value, String data, BigInteger deadline, String extra) {
        return new RawTransaction(from, "", Interpreter.ETHEREUM, nonce, gasLimit, gasPrice,
                value, data, deadline, extra);
    }

    public static RawTransaction createEvmCallTransaction(String from, String to, String dataHex) {
        return new RawTransaction(from, to, Interpreter.EVM, null, null, null,
                null, dataHex, BigInteger.ZERO, "");
    }

    public static RawTransaction createEthereumCallTransaction(String from, String to, String dataHex) {
        return new RawTransaction(from, to, Interpreter.ETHEREUM, null, null, null,
                null, dataHex, BigInteger.ZERO, "");
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Interpreter getInterpreter() {
        return interpreter;
    }

    public void setInterpreter(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public void setGasLimit(BigInteger gasLimit) {
        this.gasLimit = gasLimit;
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }

    public void setGasPrice(BigInteger gasPrice) {
        this.gasPrice = gasPrice;
    }

    public BigInteger getValue() {
        return value;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public BigInteger getDeadline() {
        return deadline;
    }

    public void setDeadline(BigInteger deadline) {
        this.deadline = deadline;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
