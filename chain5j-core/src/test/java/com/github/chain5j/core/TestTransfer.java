package com.github.chain5j.core;

import com.github.chain5j.constant.TxType;
import com.github.chain5j.crypto.Credentials;
import com.github.chain5j.crypto.Crypto;
import com.github.chain5j.crypto.RawTransaction;
import com.github.chain5j.crypto.Sign;
import com.github.chain5j.engine.SignEngine;
import com.github.chain5j.engine.WalletEngine;
import com.github.chain5j.protocol.Chain5j;
import com.github.chain5j.protocol.core.DefaultBlockParameterName;
import com.github.chain5j.protocol.core.Request;
import com.github.chain5j.protocol.core.methods.response.GetTransactionCount;
import com.github.chain5j.protocol.http.HttpService;
import org.junit.Test;

import java.math.BigInteger;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2020/6/12 22:39
 * @Copyright Copyright@2020
 */
public class TestTransfer {
    @Test
    public void baseTransfer() {
        try {
            String privateKey = "7AC46DB941941262187682BF8BCFE75D1739B9E965823176C317A6421A1C2935";
            Sign.SetCryptoName(Crypto.P256);
            Credentials credentials = WalletEngine.loadCredentialsByPrivateKey(privateKey);

            String from = "admin@chain5j.com";
            String to = "user1@chain5j.com";

            Chain5j chain5j = Chain5j.build(new HttpService("http://127.0.0.1:9545"));
            Request<?, GetTransactionCount> transactionCount = chain5j.getTransactionCount(from, DefaultBlockParameterName.LATEST);
            BigInteger nonce = transactionCount.send().getTransactionCount();

            BigInteger gasLimit = BigInteger.valueOf(4000000);
            BigInteger gasPrice = BigInteger.valueOf(0);
            BigInteger value = BigInteger.valueOf(1);
            BigInteger deadline = BigInteger.valueOf(0);
            String data = "0x02";
            String extra = "0x02";
            RawTransaction transaction = RawTransaction.createBaseTransaction(from, to, nonce, gasLimit, gasPrice, value, data, deadline, extra);
            String rawTx = SignEngine.sign(transaction, credentials);
            System.out.println(rawTx);

            String hash = SignEngine.sendTransaction(chain5j, TxType.STATE, rawTx);
            System.out.println("hash=" + hash);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
