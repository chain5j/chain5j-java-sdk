package com.github.chain5j.protocol.core.methods.request;

import java.math.BigInteger;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.github.chain5j.crypto.Interpreter;
import com.github.chain5j.utils.Numeric;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Transaction {
    public static final BigInteger DEFAULT_GAS = BigInteger.valueOf(9000);

    private String from;
    private String to;
    private Interpreter interpreter;
    private BigInteger nonce;
    private BigInteger gas;
    private BigInteger gasPrice;
    private BigInteger value;
    private String data;
    private BigInteger deadline = BigInteger.ZERO;
    //    private String signature;
    private String extra;

    public Transaction(String from, String to, Interpreter interpreter, BigInteger nonce, BigInteger gas, BigInteger gasPrice,
                       BigInteger value, String data, BigInteger deadline, String extra) {
        this.from = from;
        this.to = to;
        this.interpreter = interpreter;
        this.nonce = nonce;
        this.gas = gas;
        this.gasPrice = gasPrice;
        this.value = value;
        if (data != null) {
            this.data = Numeric.prependHexPrefix(data);
        }
        this.deadline = deadline;
        this.extra = extra;
    }

    public static Transaction createTransaction(String from, String to, Interpreter interpreter, BigInteger nonce, BigInteger gasLimit, BigInteger gasPrice,
                                                BigInteger value, String data, BigInteger deadline, String extra) {
        return new Transaction(from, to, interpreter, nonce, gasLimit, gasPrice,
                value, data, deadline, extra);
    }

    public static Transaction createBaseTransaction(String from, String to, BigInteger nonce, BigInteger gasLimit, BigInteger gasPrice,
                                                    BigInteger value, BigInteger deadline, String extra) {
        return new Transaction(from, to, Interpreter.EVM, nonce, gasLimit, gasPrice,
                value, "", deadline, extra);
    }

    public static Transaction createAccountTransaction(String from, String to, BigInteger nonce,
                                                       String data, BigInteger deadline, String extra) {
        return new Transaction(from, to, Interpreter.ACCOUNT, nonce, null, null,
                null, data, deadline, extra);
    }

    public static Transaction createLostTransaction(String from, String to, BigInteger nonce,
                                                    String data, BigInteger deadline, String extra) {
        return new Transaction(from, to, Interpreter.LOST, nonce, null, null,
                null, data, deadline, extra);
    }

    public static Transaction createEvmTransaction(String from, String to, BigInteger nonce, BigInteger gasLimit, BigInteger gasPrice,
                                                   BigInteger value, String data, BigInteger deadline, String extra) {
        return new Transaction(from, to, Interpreter.EVM, nonce, gasLimit, gasPrice,
                value, data, deadline, extra);
    }

    public static Transaction createCaTransaction(String from, String to, BigInteger nonce, BigInteger gasLimit, BigInteger gasPrice,
                                                  BigInteger value, String data, BigInteger deadline, String extra) {
        return new Transaction(from, to, Interpreter.CA, nonce, gasLimit, gasPrice,
                value, data, deadline, extra);
    }

    public static Transaction createPoeTransaction(String from, String to, BigInteger nonce, BigInteger gasLimit, BigInteger gasPrice,
                                                   BigInteger value, String data, BigInteger deadline, String extra) {
        return new Transaction(from, to, Interpreter.POE, nonce, gasLimit, gasPrice,
                value, data, deadline, extra);
    }

    public static Transaction createEthereumTransaction(String from, String to, BigInteger nonce, BigInteger gasLimit, BigInteger gasPrice,
                                                        BigInteger value, String data, BigInteger deadline, String extra) {
        return new Transaction(from, to, Interpreter.ETHEREUM, nonce, gasLimit, gasPrice,
                value, data, deadline, extra);
    }

    public static Transaction createEvmCallTransaction(String from, String to, String dataHex) {
        return new Transaction(from, to, Interpreter.EVM, null, null, null,
                null, dataHex, BigInteger.ZERO, "");
    }

    public static Transaction createEthereumCallTransaction(String from, String to, String dataHex) {
        return new Transaction(from, to, Interpreter.ETHEREUM, null, null, null,
                null, dataHex, BigInteger.ZERO, "");
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getGas() {
        return convert(gas);
    }

    public String getGasPrice() {
        return convert(gasPrice);
    }

    public String getValue() {
        return convert(value);
    }

    public String getData() {
        return data;
    }

    public String getNonce() {
        return convert(nonce);
    }

    public String getInterpreter() {
        return interpreter.getValue();
    }

    public String getDeadline() {
        return convert(deadline);
    }

    public String getExtra() {
        return extra;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setInterpreter(Interpreter interpreter) {
        this.interpreter = interpreter;
    }

    public void setNonce(BigInteger nonce) {
        this.nonce = nonce;
    }

    public void setGas(BigInteger gas) {
        this.gas = gas;
    }

    public void setGasPrice(BigInteger gasPrice) {
        this.gasPrice = gasPrice;
    }

    public void setValue(BigInteger value) {
        this.value = value;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setDeadline(BigInteger deadline) {
        this.deadline = deadline;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    private static String convert(BigInteger value) {
        if (value != null) {
            return Numeric.encodeQuantity(value);
        } else {
            return null;  // we don't want the field to be encoded if not present
        }
    }
}
