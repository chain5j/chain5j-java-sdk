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
import com.github.chain5j.protocol.core.methods.request.Account;
import com.github.chain5j.protocol.core.methods.request.AccountInfo;
import com.github.chain5j.protocol.core.methods.request.AddressStore;
import com.github.chain5j.protocol.core.methods.response.GetAccountInfo;
import com.github.chain5j.protocol.core.methods.response.SendTransaction;
import com.github.chain5j.protocol.http.HttpService;
import com.github.chain5j.utils.Numeric;
import org.junit.Test;

import java.math.BigInteger;
import java.util.TreeMap;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2020/6/11 23:03
 * @Copyright Copyright@2020
 */
public class TestAccount {
    @Test
    public void getNonce() {
        Chain5j chain5j = Chain5j.build(new HttpService("http://127.0.0.1:9545"));
        try {
            Request<?, GetAccountInfo> accountInfo = chain5j.accountInfo("admin@chain5j.com", DefaultBlockParameterName.LATEST);
            System.out.println(accountInfo.send().getAccountInfo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void register() {
        Chain5j chain5j = Chain5j.build(new HttpService("http://127.0.0.1:9545"));
        try {
            BigInteger nonce = SignEngine.getNonce(chain5j, "admin@chain5j.com", DefaultBlockParameterName.LATEST);
            System.out.println("nonce="+nonce);
            String cn = "user1";
            String domain = "chain5j.com";

            AccountInfo accountInfo = new AccountInfo(cn, domain);
            TreeMap<String, AddressStore> addresses = new TreeMap();
            addresses.put("0x51Af97344F891fAf8B2207e17944206BA530074D", new AddressStore());
            accountInfo.setAddresses(addresses);
            byte[] accountData = accountInfo.toRlpEncode();
            System.out.println("accountData="+Numeric.toHexString(accountData));

            Account.AccountTxData accountTxData = new Account.AccountTxData(Account.AccountOp.RegisterAcountOp, accountData);
            String input = accountTxData.toRlpEncodeStr();
            System.out.println("input="+input);

            RawTransaction rawTransaction = RawTransaction.createAccountTransaction(
                    "admin@chain5j.com",// 管理员账户名称
                    accountInfo.accountName(),
                    nonce,
                    input,
                    BigInteger.ZERO,
                    ""
            );

            String privateKey = "7AC46DB941941262187682BF8BCFE75D1739B9E965823176C317A6421A1C2935";
            Sign.SetCryptoName(Crypto.P256);
            Credentials credentials = WalletEngine.loadCredentialsByPrivateKey(privateKey);
            String rawTx = SignEngine.sign(rawTransaction, credentials);
            System.out.println("rawTx="+rawTx);
            Request<?, SendTransaction> sendRawTransaction = chain5j.sendRawTransaction(TxType.STATE, rawTx);
            String hash = sendRawTransaction.send().getTransactionHash();
            System.out.println("hash="+hash);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void accountInfo() {
        Chain5j chain5j = Chain5j.build(new HttpService("http://127.0.0.1:9545"));
        try {
            Request<?, GetAccountInfo> accountInfo = chain5j.accountInfo("admin@chain5j.com", DefaultBlockParameterName.LATEST);
            System.out.println(accountInfo.send().getAccountInfo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
