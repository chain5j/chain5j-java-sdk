package com.github.chain5j.protocol.core.methods.response;

import com.github.chain5j.protocol.core.Response;

public class RpcSubscribe extends Response<String> {
    public String getSubscriptionId() {
        return getResult();
    }
}
