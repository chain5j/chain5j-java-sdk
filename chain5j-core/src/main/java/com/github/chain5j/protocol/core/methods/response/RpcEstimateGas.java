package com.github.chain5j.protocol.core.methods.response;

import java.math.BigInteger;

import com.github.chain5j.protocol.core.Response;
import com.github.chain5j.utils.Numeric;

/**
 * rpc_estimateGas.
 */
public class RpcEstimateGas extends Response<String> {
    public BigInteger getAmountUsed() {
        return Numeric.decodeQuantity(getResult());
    }
}
