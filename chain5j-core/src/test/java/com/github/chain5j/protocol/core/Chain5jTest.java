package com.github.chain5j.protocol.core;

import com.github.chain5j.crypto.Credentials;
import com.github.chain5j.crypto.RawTransaction;
import com.github.chain5j.crypto.Sign;
import com.github.chain5j.engine.SignEngine;
import com.github.chain5j.engine.WalletEngine;
import com.github.chain5j.protocol.Chain5j;
import com.github.chain5j.protocol.core.methods.response.GetTransactionCount;
import com.github.chain5j.protocol.http.HttpService;
import com.github.chain5j.rlp.RlpEncoder;
import com.github.chain5j.rlp.RlpList;
import com.github.chain5j.rlp.RlpType;
import com.github.chain5j.utils.Numeric;
import org.junit.Test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2020/3/4 17:19
 * @Copyright Copyright@2020
 */
public class Chain5jTest {
    @Test
    public void Sign() {
        try {

            String privateKey = "7AC46DB941941262187682BF8BCFE75D1739B9E965823176C317A6421A1C2935";
            Sign.SetCryptoName("secp256r1");
            Credentials credentials = WalletEngine.loadCredentialsByPrivateKey(privateKey);

            String keystore = WalletEngine.getKeystore(credentials.getEcKeyPair(), "123456", false);

            String from = "admin@chain5j.com";
            String to = "user1@chain5j.com";

            Chain5j chain5j = Chain5j.build(new HttpService("http://127.0.0.1:9545"));
            Request<?, GetTransactionCount> transactionCount = chain5j.getTransactionCount(from, DefaultBlockParameterName.LATEST);
            BigInteger nonce = transactionCount.send().getTransactionCount();

            BigInteger gasLimit = BigInteger.valueOf(4000000);
            BigInteger gasPrice = BigInteger.valueOf(0);
            BigInteger value = BigInteger.valueOf(0);
            BigInteger deadline = BigInteger.valueOf(0);
            String data = "0x608060405234801561001057600080fd5b50610175806100206000396000f30060806040526004361061004c576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680631a6952301461005157806370a0823114610087575b600080fd5b610085600480360381019080803573ffffffffffffffffffffffffffffffffffffffff1690602001909291905050506100de565b005b34801561009357600080fd5b506100c8600480360381019080803573ffffffffffffffffffffffffffffffffffffffff169060200190929190505050610128565b6040518082815260200191505060405180910390f35b8073ffffffffffffffffffffffffffffffffffffffff166108fc349081150290604051600060405180830381858888f19350505050158015610124573d6000803e3d6000fd5b5050565b60008173ffffffffffffffffffffffffffffffffffffffff163190509190505600a165627a7a7230582071803ef429959970b6901abd473d00e66b517becd76295c65c6b122aa8d2991c0029";
            String extra = "0x02";
            RawTransaction transaction = RawTransaction.createEvmTransaction(from, nonce, gasLimit, gasPrice,
                    value, data, deadline, extra);


            String sign = SignEngine.sign(transaction, credentials);
            System.out.println(sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void Rlp() {
        List<RlpType> values = new ArrayList<>();
        values.add(new RlpList());
        RlpList rlpList = new RlpList(values);
        byte[] bytes = RlpEncoder.encode(rlpList);
        System.out.println(Numeric.toHexString(bytes));
    }
}
