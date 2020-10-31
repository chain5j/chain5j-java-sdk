package com.github.chain5j.sync;

import com.github.chain5j.engine.sync.BlockSyncCallback;
import com.github.chain5j.engine.sync.BlockSyncEngine;
import com.github.chain5j.engine.sync.vo.ChainBlockInfo;
import com.github.chain5j.engine.sync.vo.ChainTransactionInfo;
import com.github.chain5j.protocol.Chain5j;
import com.github.chain5j.protocol.http.HttpService;

import java.math.BigInteger;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2019-06-04 17:58
 * @Copyright Copyright@2019
 */
public class BlockSyncEngineTest {

    public static void main(String[] args) {
        BlockSyncEngine bulid = BlockSyncEngine.bulid(Chain5j.build(new HttpService("http://127.0.0.1:9545")), new BlockSyncCallback() {
            @Override
            public void saveBlock(ChainBlockInfo blockInfo) {
                System.out.println(blockInfo.toJsonString());
            }

            @Override
            public void pendingTransaction(ChainTransactionInfo transactionInfo) {
                System.out.println(transactionInfo.toString());
            }

            @Override
            public void logException(Exception e) {
                e.printStackTrace();
            }
        });
        bulid.syncBlock(BigInteger.ZERO);
    }
}
