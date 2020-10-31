package com.github.chain5j.protocol.core.methods.response;

import java.math.BigInteger;

import com.github.chain5j.protocol.core.Response;
import com.github.chain5j.utils.Numeric;

public class GetBalance extends Response<String> {
    public BigInteger getBalance() {
        return Numeric.decodeQuantity(getResult());
    }
}
