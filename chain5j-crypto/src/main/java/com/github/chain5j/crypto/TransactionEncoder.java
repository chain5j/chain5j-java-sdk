package com.github.chain5j.crypto;

import java.util.ArrayList;
import java.util.List;

import com.github.chain5j.rlp.RlpEncoder;
import com.github.chain5j.rlp.RlpList;
import com.github.chain5j.rlp.RlpString;
import com.github.chain5j.rlp.RlpType;
import com.github.chain5j.utils.Numeric;
import com.github.chain5j.utils.StringUtils;

/**
 * Create RLP encoded transaction, implementation as per p4 of the
 * <a href="http://gavwood.com/paper.pdf">yellow paper</a>.
 */
public class TransactionEncoder {

    public static byte[] signMessage(RawTransaction rawTransaction, Credentials credentials) {
        byte[] encodedTransaction = encode(rawTransaction);

        Sign.SignatureData signatureData = Sign.signMessage(
                encodedTransaction, credentials.getEcKeyPair());

        return encode(rawTransaction, signatureData);
    }

    public static byte[] signMessage(
            RawTransaction rawTransaction, int chainId, Credentials credentials) {
        byte[] encodedTransaction = encode(rawTransaction, chainId);
        Sign.SignatureData signatureData = Sign.signMessage(
                encodedTransaction, credentials.getEcKeyPair());

        Sign.SignatureData eip155SignatureData = createEip155SignatureData(signatureData, chainId);
        return encode(rawTransaction, eip155SignatureData);
    }

    public static Sign.SignatureData createEip155SignatureData(
            Sign.SignatureData signatureData, int chainId) {
//        int v = (signatureData.getV() + (chainId << 1) + 8);
        int v = signatureData.getV();

        return new Sign.SignatureData(
                v, signatureData.getR(), signatureData.getS());
    }

    public static byte[] encode(RawTransaction rawTransaction) {
        return encode(rawTransaction, null);
    }

    public static byte[] encode(RawTransaction rawTransaction, int chainId) {
        Sign.SignatureData signatureData = new Sign.SignatureData(
                chainId, new byte[]{}, new byte[]{});
        return encode(rawTransaction, signatureData);
    }

    private static byte[] encode(RawTransaction rawTransaction, Sign.SignatureData signatureData) {
        List<RlpType> values = asRlpValues(rawTransaction, signatureData);
        RlpList rlpList = new RlpList(values);
        return RlpEncoder.encode(rlpList);
    }

    static List<RlpType> asRlpValues(
            RawTransaction rawTransaction, Sign.SignatureData signatureData) {
        List<RlpType> result = new ArrayList<>();

        // from
        result.add(RlpString.create(rawTransaction.getFrom()));
        // an empty to address (contract creation) should not be encoded as a numeric 0 value
        // to
        String to = rawTransaction.getTo();
        if (to != null && to.length() > 0) {
            // addresses that start with zeros should be encoded with the zeros included, not
            // as numeric values
//            if (to.startsWith("0x")) {
//                result.add(RlpString.create(Numeric.hexStringToByteArray(to)));
//            } else {
//                result.add(RlpString.create(to.getBytes()));
//            }
            result.add(RlpString.create(to.getBytes()));
        } else {
            result.add(RlpString.create(""));
        }
        // Interpreter
        result.add(RlpString.create(rawTransaction.getInterpreter().getValue()));
        // nonce
        if (rawTransaction.getNonce() == null) {
            result.add(RlpString.create(""));
        } else {
            result.add(RlpString.create(rawTransaction.getNonce()));
        }
        // gasLimit
        if (rawTransaction.getGasLimit() == null) {
            result.add(RlpString.create(""));
        } else {
            result.add(RlpString.create(rawTransaction.getGasLimit()));
        }
        // gasPrice
        if (rawTransaction.getGasPrice() == null) {
            result.add(RlpString.create(""));
        } else {
            result.add(RlpString.create(rawTransaction.getGasPrice()));
        }
        // value
        if (rawTransaction.getValue() == null) {
            result.add(RlpString.create(""));
        } else {
            result.add(RlpString.create(rawTransaction.getValue()));
        }
//        // input
        byte[] data = Numeric.hexStringToByteArray(rawTransaction.getData());
        result.add(RlpString.create(data));
        // deadline
        if (rawTransaction.getDeadline() == null) {
            result.add(RlpString.create(""));
        } else {
            result.add(RlpString.create(rawTransaction.getDeadline()));
        }
        // token (if it is not,does not add to rlp)
//        String token = rawTransaction.getToken();
//        if (token != null && token.length() > 0) {
//            // addresses that start with zeros should be encoded with the zeros included, not
//            // as numeric values
//            if (token.startsWith("0x")) {
//                result.add(RlpString.create(Numeric.hexStringToByteArray(token)));
//            } else {
//                result.add(RlpString.create(token.getBytes()));
//            }
//        } else {
//            if (rawTransaction.getHasToken()) {
//                result.add(RlpString.create(""));
//            }
//        }

        if (signatureData != null) {
            Sign.SignResult signResult = new Sign.SignResult("P256", null, signatureData.getSignature());
//            System.out.println("签名数据=" + signResult.toRlpEncodeStr());
            result.add(RlpString.create(signResult.toRlpEncode()));

            String extra = rawTransaction.getExtra();
            if (StringUtils.isEmpty(extra)) {
                result.add(RlpString.create(""));
            } else {
                if (extra.startsWith("0x")) {
                    result.add(RlpString.create(Numeric.hexStringToByteArray(extra)));
                } else {
                    result.add(RlpString.create(extra.getBytes()));
                }
            }
        } else {
            result.add(RlpList.createNil());
            result.add(RlpList.createNil());
        }
        return result;
    }
}

