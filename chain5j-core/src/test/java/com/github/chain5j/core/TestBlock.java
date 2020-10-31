package com.github.chain5j.core;

import com.github.chain5j.protocol.Chain5j;
import com.github.chain5j.protocol.core.DefaultBlockParameter;
import com.github.chain5j.protocol.core.Request;
import com.github.chain5j.protocol.core.methods.response.BlockHeight;
import com.github.chain5j.protocol.core.methods.response.BlockInfo;
import com.github.chain5j.protocol.http.HttpService;
import org.junit.Test;

import java.io.IOException;
import java.math.BigInteger;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2020/6/12 22:14
 * @Copyright Copyright@2020
 */
public class TestBlock {
    @Test
    public void getBlockByHeight() {
        Chain5j chain5j = Chain5j.build(new HttpService("http://127.0.0.1:9545"));
        Request<?, BlockHeight> blockHeightRequest = chain5j.blockHeight();

        try {
            BigInteger bigInteger = blockHeightRequest.send().blockHeight();
            System.out.println(bigInteger);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void GetBlockByNumber() {
        Chain5j chain5j = Chain5j.build(new HttpService("http://127.0.0.1:9545"));
        try {
            BigInteger currentBlockNumber = new BigInteger("6");
            Request<?, BlockInfo> ethBlockRequest = chain5j.getBlockByNumber(DefaultBlockParameter.valueOf(currentBlockNumber));
            BlockInfo.Block block = ethBlockRequest.send().getBlock();
            System.out.println(block);
            Object obj = block.getTransactions().get(0);
            System.out.println(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void GetBlockByHash() {
        Chain5j chain5j = Chain5j.build(new HttpService("http://127.0.0.1:9545"));
        try {
            Request<?, BlockInfo> ethBlockRequest = chain5j.getBlockByHash("0xe62cc6a7428a8e08c406f34ddc1d10768b05afc0bf222f439d63f859c8ff8b0e");
            BlockInfo.Block block = ethBlockRequest.send().getBlock();
            System.out.println(block);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
