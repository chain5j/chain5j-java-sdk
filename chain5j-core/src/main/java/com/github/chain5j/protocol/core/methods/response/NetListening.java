package com.github.chain5j.protocol.core.methods.response;

import com.github.chain5j.protocol.core.Response;

/**
 * net_listening.
 */
public class NetListening extends Response<Boolean> {
    public boolean isListening() {
        return getResult();
    }
}
