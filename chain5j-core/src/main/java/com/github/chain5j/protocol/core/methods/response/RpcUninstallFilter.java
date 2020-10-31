package com.github.chain5j.protocol.core.methods.response;

import com.github.chain5j.protocol.core.Response;

public class RpcUninstallFilter extends Response<Boolean> {
    public boolean isUninstalled() {
        return getResult();
    }
}
