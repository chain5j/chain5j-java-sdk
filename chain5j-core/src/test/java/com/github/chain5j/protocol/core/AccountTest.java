package com.github.chain5j.protocol.core;

import com.github.chain5j.protocol.Chain5j;
import com.github.chain5j.protocol.core.methods.response.GetAccountInfo;
import com.github.chain5j.protocol.http.HttpService;
import org.junit.Test;

/**
 * Description:
 * </p>
 *
 * @Author: xwc1125
 * @Date: 2020-03-04 14:19:38
 * @Copyright Copyright@2020
 */
public class AccountTest {

    @Test
    public void accountInfo() {
        try {
            Chain5j chain5j = Chain5j.build(new HttpService("http://127.0.0.1:9545"));
            Request<?, GetAccountInfo> ethGetToken = chain5j.accountInfo("admin@chain5j.com", DefaultBlockParameterName.LATEST);
            System.out.println(ethGetToken.send().getAccountInfo());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
