package com.github.chain5j.protocol.core;

import com.github.chain5j.crypto.Credentials;
import com.github.chain5j.crypto.Sign;
import com.github.chain5j.engine.WalletEngine;
import org.junit.Test;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2020/3/5 16:06
 * @Copyright Copyright@2020
 */
public class P256Test {
    @Test
    public void TestP256() {
        // 0x92c8cae42a94045670cbb0bfcf8f790d9f8097e7
        String privateKey = "7AC46DB941941262187682BF8BCFE75D1739B9E965823176C317A6421A1C2935";
        Sign.SetCryptoName("secp256r1");
        Credentials credentials = WalletEngine.loadCredentialsByPrivateKey(privateKey);
        System.out.println(credentials.getAddress());
    }
}
