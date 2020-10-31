package com.github.chain5j.core;

import com.github.chain5j.crypto.Credentials;
import com.github.chain5j.crypto.Crypto;
import com.github.chain5j.crypto.Sign;
import com.github.chain5j.engine.WalletEngine;
import org.junit.Test;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2020/6/11 22:36
 * @Copyright Copyright@2020
 */
public class TestWallet {

    // 生成私钥
    @Test
    public void TestGenePrivateKey() {
        try {
            // 使用P256需要设置
            Sign.SetCryptoName(Crypto.P256);
            String privateKey = WalletEngine.generatePrivateKey(Crypto.P256);
            System.out.println(privateKey);
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    // 生成keystore
    @Test
    public void TestGeneKeystore() {
        try {
            // 使用P256需要设置
            Sign.SetCryptoName(Crypto.P256);// P256
            String keystore = WalletEngine.generateKeystore(Crypto.P256, "123456", false);
            System.out.println(keystore);

            // 解密keystore
            Sign.SetCryptoName(Crypto.P256);
            Credentials credentials = WalletEngine.loadCredentialsByKeyStore("123456", keystore);
            String address = credentials.getAddress();
            System.out.println(address);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void TestGetKeystore() {
        try {
//            String privateKey = "7AC46DB941941262187682BF8BCFE75D1739B9E965823176C317A6421A1C2935";
            String privateKey = "2af10a20cc358910cd17e0a3c39add9a8a126e5aa281d630dc6163d3416c347b";
            Sign.SetCryptoName(Crypto.P256);
            Credentials credentials = WalletEngine.loadCredentialsByPrivateKey(privateKey);
            System.out.println(credentials.getAddress());

            String keystore = WalletEngine.getKeystore(credentials.getEcKeyPair(), "123456", false);
            System.out.println(keystore);

            Credentials credentials1 = WalletEngine.loadCredentialsByKeyStore("123456", keystore);
            System.out.println(credentials1.getAddress());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
