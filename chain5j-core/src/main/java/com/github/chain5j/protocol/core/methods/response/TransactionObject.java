package com.github.chain5j.protocol.core.methods.response;

import com.google.gson.Gson;

import java.math.BigInteger;

public class TransactionObject {
    /**
     * gasLimit : 30000
     * input :
     * signature : 0xf849845032353680b84108cffd38f895862ff24337a8973587401b3f7ad70e2c2f1bdf71aed9e03c6fbf15685a0214a83967230a6a2bbf00502c823fc7193123456c79250e966374210d01
     * extra :
     * from : user1@chain5j.com
     * interpreter : chain5j.base
     * to : admin@dev.chain5j.com
     * deadline : 0
     * nonce : 1
     * value : 0x16
     * hash : 0xe4fc041bded0d0f6ace5a1d7a13789c01110c30232ba13b1c0b57a1d11423f6a
     * gasPrice : 0
     */
    private BigInteger gasLimit;
    private String input;
    private String signature;
    private String extra;
    private String from;
    private String interpreter;
    private String to;
    private BigInteger deadline;
    private BigInteger nonce;
    private String value;
    private String hash;
    private BigInteger gasPrice;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public void setGasLimit(BigInteger gasLimit) {
        this.gasLimit = gasLimit;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setInterpreter(String interpreter) {
        this.interpreter = interpreter;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setDeadline(BigInteger deadline) {
        this.deadline = deadline;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public void setGasPrice(BigInteger gasPrice) {
        this.gasPrice = gasPrice;
    }

    public BigInteger getGasLimit() {
        return gasLimit;
    }

    public String getInput() {
        return input;
    }

    public String getSignature() {
        return signature;
    }

    public String getExtra() {
        return extra;
    }

    public String getFrom() {
        return from;
    }

    public String getInterpreter() {
        return interpreter;
    }

    public String getTo() {
        return to;
    }

    public BigInteger getDeadline() {
        return deadline;
    }

    public BigInteger getNonce() {
        return nonce;
    }

    public String getValue() {
        return value;
    }

    public String getHash() {
        return hash;
    }

    public BigInteger getGasPrice() {
        return gasPrice;
    }
}
