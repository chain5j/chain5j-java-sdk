package com.github.chain5j.protocol.core.methods.response;

import com.github.chain5j.protocol.core.Response;

/**
 * net_version.
 */
public class NetVersion extends Response<String> {
    public String getNetVersion() {
        return getResult();
    }
}
