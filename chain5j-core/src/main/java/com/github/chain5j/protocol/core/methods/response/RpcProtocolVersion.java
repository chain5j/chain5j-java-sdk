package com.github.chain5j.protocol.core.methods.response;

import com.github.chain5j.protocol.core.Response;

/**
 * rpc_protocolVersion.
 */
public class RpcProtocolVersion extends Response<String> {
    public String getProtocolVersion() {
        return getResult();
    }
}
