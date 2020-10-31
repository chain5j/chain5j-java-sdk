package com.github.chain5j.protocol.core.methods.response;

import java.math.BigInteger;

import com.github.chain5j.protocol.core.Response;
import com.github.chain5j.utils.Numeric;

/**
 * eth_getTransactionCount.
 */
public class GetTransactionCount extends Response<String> {
    public BigInteger getTransactionCount() {
        return Numeric.decodeQuantity(getResult());
    }
}
