package com.github.chain5j.protocol.core.methods.response;

import com.github.chain5j.protocol.core.Response;

/**
 * rpc_sendRawTransaction.
 */
public class SendRawTransaction extends Response<String> {
    public String getTransactionHash() {
        return getResult();
    }
}
