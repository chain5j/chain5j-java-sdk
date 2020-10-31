package com.github.chain5j.core;

import com.github.chain5j.engine.TransactionEngine;
import com.github.chain5j.protocol.Chain5j;
import com.github.chain5j.protocol.core.methods.response.Transaction;
import com.github.chain5j.protocol.core.methods.response.TransactionReceipt;
import com.github.chain5j.protocol.http.HttpService;
import org.junit.Test;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2020/6/12 22:12
 * @Copyright Copyright@2020
 */
public class TestTransaction {

    @Test
    public void getTransactionByHash() {
        Chain5j chain5j = Chain5j.build(new HttpService("http://127.0.0.1:9545"));
        try {
            Transaction transaction = null;
            transaction = TransactionEngine.getTransaction(chain5j, "0x0b294ce4064ff8b78a452b2bdbd2afa25c1c4a0f7c8cab27acf5918760f0067a");
            System.out.println(transaction);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void getTransactionReceipt() {
        Chain5j chain5j = Chain5j.build(new HttpService("http://127.0.0.1:9545"));
        try {
            TransactionReceipt transactionReceipt = TransactionEngine.getTransactionReceipt(chain5j, "0xd713d93ff949bcbc727e9133184a3614888c7094b8bc0235d716fff051f05fd7");
            System.out.println(transactionReceipt);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
