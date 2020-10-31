package com.github.chain5j.protocol;

import java.util.concurrent.ScheduledExecutorService;

import com.github.chain5j.protocol.core.JsonRpc2_0Chain5j;
import com.github.chain5j.protocol.core.Pangu;
import com.github.chain5j.protocol.rx.Chain5jRx;

/**
 * JSON-RPC Request object building factory.
 */
public interface Chain5j extends Pangu, Chain5jRx {

    static Chain5j build(Chain5jService chain5jService) {
        return new JsonRpc2_0Chain5j(chain5jService);
    }

    /**
     * Construct a new Web3j instance.
     *
     * @param chain5jService         web3j service instance - i.e. HTTP or IPC
     * @param pollingInterval          polling interval for responses from network nodes
     * @param scheduledExecutorService executor service to use for scheduled tasks.
     *                                 <strong>You are responsible for terminating this thread
     *                                 pool</strong>
     * @return new Web3j instance
     */
    static Chain5j build(
            Chain5jService chain5jService, long pollingInterval,
            ScheduledExecutorService scheduledExecutorService) {
        return new JsonRpc2_0Chain5j(chain5jService, pollingInterval, scheduledExecutorService);
    }

    /**
     * Shutdowns a Web3j instance and closes opened resources.
     */
    void shutdown();
}
