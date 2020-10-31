package com.github.chain5j.protocol.core.methods.response;

import com.github.chain5j.protocol.core.Response;

public class SendTransaction extends Response<String> {
    public String getTransactionHash() {
        return getResult();
    }
}
