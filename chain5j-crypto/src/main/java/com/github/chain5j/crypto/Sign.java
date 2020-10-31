package com.github.chain5j.crypto;

import java.math.BigInteger;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.chain5j.rlp.RlpEncoder;
import com.github.chain5j.rlp.RlpList;
import com.github.chain5j.rlp.RlpString;
import com.github.chain5j.rlp.RlpType;
import com.github.chain5j.utils.Assertions;
import com.github.chain5j.utils.Numeric;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.asn1.x9.X9IntegerConverter;
import org.bouncycastle.crypto.ec.CustomNamedCurves;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.ECAlgorithms;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.math.ec.FixedPointCombMultiplier;
import org.bouncycastle.math.ec.custom.sec.SecP256K1Curve;

/**
 * <p>Transaction signing logic.</p>
 *
 * <p>Adapted from the
 * <a href="https://github.com/bitcoinj/bitcoinj/blob/master/core/src/main/java/org/bitcoinj/core/ECKey.java">
 * BitcoinJ ECKey</a> implementation.
 */
public class Sign {

//    public static String CryptoName = "secp256k1";// S256
    public static String CryptoName = "secp256r1";// P256

//    public static final X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName(CryptoName);
//    static final ECDomainParameters CURVE = new ECDomainParameters(
//            CURVE_PARAMS.getCurve(), CURVE_PARAMS.getG(), CURVE_PARAMS.getN(), CURVE_PARAMS.getH());
//    static final BigInteger HALF_CURVE_ORDER = getX9ECParameters().getN().shiftRight(1);

    static final String MESSAGE_PREFIX = "\u0019Ethereum Signed Message:\n";

    static ECDomainParameters getCURVE() {
        return new ECDomainParameters(
                getX9ECParameters().getCurve(), getX9ECParameters().getG(), getX9ECParameters().getN(), getX9ECParameters().getH());
    }

    static BigInteger getHALF_CURVE_ORDER() {
        return getX9ECParameters().getN().shiftRight(1);
    }

    static X9ECParameters getX9ECParameters() {
        return CustomNamedCurves.getByName(CryptoName);
    }

    static byte[] getEthereumMessagePrefix(int messageLength) {
        return MESSAGE_PREFIX.concat(String.valueOf(messageLength)).getBytes();
    }

    static byte[] getEthereumMessageHash(byte[] message) {
        byte[] prefix = getEthereumMessagePrefix(message.length);

        byte[] result = new byte[prefix.length + message.length];
        System.arraycopy(prefix, 0, result, 0, prefix.length);
        System.arraycopy(message, 0, result, prefix.length, message.length);

        return Hash.sha3(result);
    }

    public static void SetCryptoName(String name) {
        CryptoName = name;
    }

    public static SignatureData signPrefixedMessage(byte[] message, ECKeyPair keyPair) {
        return signMessage(getEthereumMessageHash(message), keyPair, false);
    }

    public static SignatureData signMessage(byte[] message, ECKeyPair keyPair) {
        return signMessage(message, keyPair, true);
    }

    public static SignatureData signMessage(byte[] message, ECKeyPair keyPair, boolean needToHash) {
        BigInteger publicKey = keyPair.getPublicKey();

        byte[] messageHash;
        if (needToHash) {
            messageHash = Hash.sha3(message);
        } else {
            messageHash = message;
        }

//        System.out.println("keyPair="+ keyPair.getPublicKey());
//        System.out.println("rlpHash="+ Hex.toHexString(messageHash));
        ECDSASignature sig = keyPair.sign(messageHash);
        // Now we have to work backwards to figure out the recId needed to recover the signature.
        int recId = -1;
        for (int i = 0; i < 4; i++) {
            BigInteger k = recoverFromSignature(i, sig, messageHash);
            if (k != null && k.equals(publicKey)) {
                recId = i;
                break;
            }
        }
        if (recId == -1) {
            throw new RuntimeException(
                    "Could not construct a recoverable key. Are your credentials valid?");
        }

//        int headerByte = recId + 27;// TODO 和27进行处理
        int headerByte = recId;

        // 1 header + 32 bytes for R + 32 bytes for S
        byte v = (byte) headerByte;
        byte[] r = Numeric.toBytesPadded(sig.r, 32);
        byte[] s = Numeric.toBytesPadded(sig.s, 32);

        return new SignatureData(v, r, s);
    }

    /**
     * <p>Given the components of a signature and a selector value, recover and return the public
     * key that generated the signature according to the algorithm in SEC1v2 section 4.1.6.</p>
     *
     * <p>The recId is an index from 0 to 3 which indicates which of the 4 possible keys is the
     * correct one. Because the key recovery operation yields multiple potential keys, the correct
     * key must either be stored alongside the
     * signature, or you must be willing to try each recId in turn until you find one that outputs
     * the key you are expecting.</p>
     *
     * <p>If this method returns null it means recovery was not possible and recId should be
     * iterated.</p>
     *
     * <p>Given the above two points, a correct usage of this method is inside a for loop from
     * 0 to 3, and if the output is null OR a key that is not the one you expect, you try again
     * with the next recId.</p>
     *
     * @param recId   Which possible key to recover.
     * @param sig     the R and S components of the signature, wrapped.
     * @param message Hash of the data that was signed.
     * @return An ECKey containing only the public part, or null if recovery wasn't possible.
     */
    public static BigInteger recoverFromSignature(int recId, ECDSASignature sig, byte[] message) {
        Assertions.verifyPrecondition(recId >= 0, "recId must be positive");
        Assertions.verifyPrecondition(sig.r.signum() >= 0, "r must be positive");
        Assertions.verifyPrecondition(sig.s.signum() >= 0, "s must be positive");
        Assertions.verifyPrecondition(message != null, "message cannot be null");

        // 1.0 For j from 0 to h   (h == recId here and the loop is outside this function)
        //   1.1 Let x = r + jn
        BigInteger n = getCURVE().getN();  // Curve order.
        BigInteger i = BigInteger.valueOf((long) recId / 2);
        BigInteger x = sig.r.add(i.multiply(n));
        //   1.2. Convert the integer x to an octet string X of length mlen using the conversion
        //        routine specified in Section 2.3.7, where mlen = ⌈(log2 p)/8⌉ or mlen = ⌈m/8⌉.
        //   1.3. Convert the octet string (16 set binary digits)||X to an elliptic curve point R
        //        using the conversion routine specified in Section 2.3.4. If this conversion
        //        routine outputs "invalid", then do another iteration of Step 1.
        //
        // More concisely, what these points mean is to use X as a compressed public key.
        BigInteger prime = SecP256K1Curve.q;
        if (x.compareTo(prime) >= 0) {
            // Cannot have point co-ordinates larger than this as everything takes place modulo Q.
            return null;
        }
        // Compressed keys require you to know an extra bit of data about the y-coord as there are
        // two possibilities. So it's encoded in the recId.
        ECPoint R = decompressKey(x, (recId & 1) == 1);
        //   1.4. If nR != point at infinity, then do another iteration of Step 1 (callers
        //        responsibility).
        if (!R.multiply(n).isInfinity()) {
            return null;
        }
        //   1.5. Compute e from M using Steps 2 and 3 of ECDSA signature verification.
        BigInteger e = new BigInteger(1, message);
        //   1.6. For k from 1 to 2 do the following.   (loop is outside this function via
        //        iterating recId)
        //   1.6.1. Compute a candidate public key as:
        //               Q = mi(r) * (sR - eG)
        //
        // Where mi(x) is the modular multiplicative inverse. We transform this into the following:
        //               Q = (mi(r) * s ** R) + (mi(r) * -e ** G)
        // Where -e is the modular additive inverse of e, that is z such that z + e = 0 (mod n).
        // In the above equation ** is point multiplication and + is point addition (the EC group
        // operator).
        //
        // We can find the additive inverse by subtracting e from zero then taking the mod. For
        // example the additive inverse of 3 modulo 11 is 8 because 3 + 8 mod 11 = 0, and
        // -3 mod 11 = 8.
        BigInteger eInv = BigInteger.ZERO.subtract(e).mod(n);
        BigInteger rInv = sig.r.modInverse(n);
        BigInteger srInv = rInv.multiply(sig.s).mod(n);
        BigInteger eInvrInv = rInv.multiply(eInv).mod(n);
        ECPoint q = ECAlgorithms.sumOfTwoMultiplies(getCURVE().getG(), eInvrInv, R, srInv);

        byte[] qBytes = q.getEncoded(false);
        // We remove the prefix
        return new BigInteger(1, Arrays.copyOfRange(qBytes, 1, qBytes.length));
    }

    /**
     * Decompress a compressed public key (x co-ord and low-bit of y-coord).
     */
    private static ECPoint decompressKey(BigInteger xBN, boolean yBit) {
        X9IntegerConverter x9 = new X9IntegerConverter();
        byte[] compEnc = x9.integerToBytes(xBN, 1 + x9.getByteLength(getCURVE().getCurve()));
        compEnc[0] = (byte) (yBit ? 0x03 : 0x02);
        return getCURVE().getCurve().decodePoint(compEnc);
    }

    /**
     * Given an arbitrary piece of text and an Ethereum message signature encoded in bytes,
     * returns the public key that was used to sign it. This can then be compared to the expected
     * public key to determine if the signature was correct.
     *
     * @param message       RLP encoded message.
     * @param signatureData The message signature components
     * @return the public key used to sign the message
     * @throws SignatureException If the public key could not be recovered or if there was a
     *                            signature format error.
     */
    public static BigInteger signedMessageToKey(
            byte[] message, SignatureData signatureData) throws SignatureException {
        return signedMessageHashToKey(Hash.sha3(message), signatureData);
    }

    /**
     * Given an arbitrary message and an Ethereum message signature encoded in bytes,
     * returns the public key that was used to sign it. This can then be compared to the
     * expected public key to determine if the signature was correct.
     *
     * @param message       The message.
     * @param signatureData The message signature components
     * @return the public key used to sign the message
     * @throws SignatureException If the public key could not be recovered or if there was a
     *                            signature format error.
     */
    public static BigInteger signedPrefixedMessageToKey(
            byte[] message, SignatureData signatureData) throws SignatureException {
        return signedMessageHashToKey(getEthereumMessageHash(message), signatureData);
    }

    static BigInteger signedMessageHashToKey(
            byte[] messageHash, SignatureData signatureData) throws SignatureException {

        byte[] r = signatureData.getR();
        byte[] s = signatureData.getS();
        Assertions.verifyPrecondition(r != null && r.length == 32, "r must be 32 bytes");
        Assertions.verifyPrecondition(s != null && s.length == 32, "s must be 32 bytes");

        int header = signatureData.getV() & 0xFF;
        // The header byte: 0x1B = first key with even y, 0x1C = first key with odd y,
        //                  0x1D = second key with even y, 0x1E = second key with odd y
        // TODO no chainId
//        if (header < 27 || header > 34) {
//            throw new SignatureException("Header byte out of range: " + header);
//        }

        ECDSASignature sig = new ECDSASignature(
                new BigInteger(1, signatureData.getR()),
                new BigInteger(1, signatureData.getS()));

        // TODO no chainId
//        int recId = header - 27;
        int recId = header;
        BigInteger key = recoverFromSignature(recId, sig, messageHash);
        if (key == null) {
            throw new SignatureException("Could not recover public key from signature");
        }
        return key;
    }

    /**
     * Returns public key from the given private key.
     *
     * @param privKey the private key to derive the public key from
     * @return BigInteger encoded public key
     */
    public static BigInteger publicKeyFromPrivate(BigInteger privKey) {
        ECPoint point = publicPointFromPrivate(privKey);

        byte[] encoded = point.getEncoded(false);
        return new BigInteger(1, Arrays.copyOfRange(encoded, 1, encoded.length));  // remove prefix
    }

    /**
     * Returns public key point from the given private key.
     *
     * @param privKey the private key to derive the public key from
     * @return ECPoint public key
     */
    public static ECPoint publicPointFromPrivate(BigInteger privKey) {
        /*
         * TODO: FixedPointCombMultiplier currently doesn't support scalars longer than the group
         * order, but that could change in future versions.
         */
        if (privKey.bitLength() > getCURVE().getN().bitLength()) {
            privKey = privKey.mod(getCURVE().getN());
        }
        return new FixedPointCombMultiplier().multiply(getCURVE().getG(), privKey);
    }

    /**
     * Returns public key point from the given curve.
     *
     * @param bits representing the point on the curve
     * @return BigInteger encoded public key
     */
    public static BigInteger publicFromPoint(byte[] bits) {
        return new BigInteger(1, Arrays.copyOfRange(bits, 1, bits.length));  // remove prefix
    }

    public static class SignatureData {
        private final int v;
        private final byte[] r;
        private final byte[] s;

        public SignatureData(int v, byte[] r, byte[] s) {
            this.v = v;
            this.r = r;
            this.s = s;
        }

        public int getV() {
            return v;
        }

        public byte[] getR() {
            return r;
        }

        public byte[] getS() {
            return s;
        }

        public byte[] getSignature() {
            byte[] arraycopy = arraycopy(r, s);
            arraycopy = arraycopy(arraycopy, intToBytes(v, 1));
            return arraycopy;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            SignatureData that = (SignatureData) o;

            if (v != that.v) {
                return false;
            }
            if (!Arrays.equals(r, that.r)) {
                return false;
            }
            return Arrays.equals(s, that.s);
        }

        @Override
        public int hashCode() {
            int result = (int) v;
            result = 31 * result + Arrays.hashCode(r);
            result = 31 * result + Arrays.hashCode(s);
            return result;
        }
    }

    /**
     * 将整数转换为byte数组并指定长度
     *
     * @param a      整数
     * @param length 指定长度
     * @return
     */
    public static byte[] intToBytes(int a, int length) {
        byte[] bs = new byte[length];
        for (int i = bs.length - 1; i >= 0; i--) {
            bs[i] = (byte) (a % 255);
            a = a / 255;
        }
        return bs;
    }

    public static class SignResult {
        private final String name;
        private final byte[] pubKey;
        private final byte[] signature;

        public SignResult(String name, byte[] pubKey, byte[] signature) {
            this.name = name;
            this.pubKey = pubKey;
            this.signature = signature;
        }

        public String getName() {
            return name;
        }

        public byte[] getPubKey() {
            return pubKey;
        }

        public byte[] getSignature() {
            return signature;
        }

        public String toRlpEncodeStr() {
            byte[] bytes = toRlpEncode();
            return Numeric.toHexString(bytes);
        }

        public byte[] toRlpEncode() {
            RlpType rlpList = toRlpType();
            return RlpEncoder.encode(rlpList);
        }

        public RlpType toRlpType() {
            List<RlpType> result = new ArrayList<>();
            result.add(RlpString.create(name));
            if (pubKey == null || pubKey.length == 0) {
                result.add(RlpString.create(""));
            } else {
                result.add(RlpString.create(pubKey));
            }
            result.add(RlpString.create(signature));
            return new RlpList(result);
        }
    }

    // 使用 Arrays.copyOf() 方法,但要在 java6++版本中
    public static byte[] arraycopy(byte[] first, byte[] second) {
        byte[] result = Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
