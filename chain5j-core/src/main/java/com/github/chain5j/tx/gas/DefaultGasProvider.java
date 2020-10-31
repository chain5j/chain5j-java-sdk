package com.github.chain5j.tx.gas;

import java.math.BigInteger;

public class DefaultGasProvider extends StaticGasProvider {
    public static final BigInteger GAS_LIMIT = new BigInteger("4700000");
    public static final BigInteger GAS_PRICE = new BigInteger("20000000000");

    public DefaultGasProvider() {
        super(GAS_PRICE, GAS_LIMIT);
    }
}
