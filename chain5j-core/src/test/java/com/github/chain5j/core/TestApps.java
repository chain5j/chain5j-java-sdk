package com.github.chain5j.core;

import com.github.chain5j.engine.SignEngine;
import com.github.chain5j.protocol.Chain5j;
import com.github.chain5j.protocol.core.DefaultBlockParameterName;
import com.github.chain5j.protocol.http.HttpService;
import org.junit.Test;

import java.math.BigInteger;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2020/6/11 22:59
 * @Copyright Copyright@2020
 */
public class TestApps {

    @Test
    public void getBalance() {
        Chain5j chain5j = Chain5j.build(new HttpService("http://127.0.0.1:9545"));
        try {
            BigInteger balance = SignEngine.getBalance(chain5j, "admin@chain5j.com");
            System.out.println(balance);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getNonce() {
        Chain5j chain5j = Chain5j.build(new HttpService("http://127.0.0.1:9545"));
        try {
            BigInteger nonce = SignEngine.getNonce(chain5j, "admin@chain5j.com", DefaultBlockParameterName.LATEST);
            System.out.println(nonce);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
