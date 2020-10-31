package com.github.chain5j.protocol.core.methods.response;

import java.math.BigInteger;

import com.github.chain5j.protocol.core.Response;
import com.github.chain5j.utils.Numeric;

public class RpcGasPrice extends Response<String> {
    public BigInteger getGasPrice() {
        return Numeric.decodeQuantity(getResult());
    }
}
