package com.github.chain5j.crypto;

import java.math.BigInteger;

import com.github.chain5j.rlp.RlpDecoder;
import com.github.chain5j.rlp.RlpList;
import com.github.chain5j.rlp.RlpString;
import com.github.chain5j.utils.Numeric;

public class TransactionDecoder {

    public static RawTransaction decode(String hexTransaction) {
        byte[] transaction = Numeric.hexStringToByteArray(hexTransaction);
        RlpList rlpList = RlpDecoder.decode(transaction);
        RlpList values = (RlpList) rlpList.getValues().get(0);

        String from = ((RlpString) values.getValues().get(0)).asString();
        String to = ((RlpString) values.getValues().get(1)).asString();
        String interpreter = ((RlpString) values.getValues().get(2)).asString();
        BigInteger nonce = ((RlpString) values.getValues().get(3)).asPositiveBigInteger();
        BigInteger gasLimit = ((RlpString) values.getValues().get(4)).asPositiveBigInteger();
        BigInteger gasPrice = ((RlpString) values.getValues().get(5)).asPositiveBigInteger();
        BigInteger value = ((RlpString) values.getValues().get(6)).asPositiveBigInteger();

        String data = ((RlpString) values.getValues().get(7)).asString();
        BigInteger deadline = Numeric.toBigInt(((RlpString) values.getValues().get(8)).getBytes());
        Sign.SignatureData signatureData = null;
        String extra = null;
        if (values.getValues().size() > 9) {
            byte[] signResult = ((RlpString) values.getValues().get(9)).getBytes();
            RlpList rlpList2 = RlpDecoder.decode(signResult);
            RlpList values2 = (RlpList) rlpList2.getValues().get(0);
            String name = ((RlpString) values2.getValues().get(0)).asString();
            byte[] pubKey = ((RlpString) values2.getValues().get(1)).getBytes();
            byte[] signatureData1 = ((RlpString) values2.getValues().get(2)).getBytes();

            byte[] r = Numeric.toBytesPadded(Numeric.toBigInt(subByte(signatureData1, 0, 32)), 32);
            byte[] s = Numeric.toBytesPadded(Numeric.toBigInt(subByte(signatureData1, 32, 64)), 32);
            BigInteger v = Numeric.toBigInt(subByte(signatureData1, 64, 65));

            signatureData = new Sign.SignatureData(v.intValue(), r, s);

        }
        if (values.getValues().size() > 10) {
            extra = ((RlpString) values.getValues().get(11)).asString();
        }

        if (signatureData != null) {
            return new SignedRawTransaction(from, to, Interpreter.Parse(interpreter), nonce, gasLimit, gasPrice,
                    value, data, deadline, extra, signatureData);
        } else {
            return RawTransaction.createTransaction(from, to, Interpreter.Parse(interpreter), nonce, gasLimit, gasPrice,
                    value, data, deadline, extra);
        }
    }

    /**
     * 截取byte数组   不改变原数组
     *
     * @param b      原数组
     * @param off    偏差值（索引）
     * @param length 长度
     * @return 截取后的数组
     */
    public static byte[] subByte(byte[] b, int off, int length) {
        byte[] b1 = new byte[length];
        System.arraycopy(b, off, b1, 0, length);
        return b1;
    }
}
