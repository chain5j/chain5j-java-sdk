package com.github.chain5j.abi.datatypes.generated;

import java.math.BigInteger;
import com.github.chain5j.abi.datatypes.Int;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use com.github.chain5j.codegen.AbiTypesGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 */
public class Int160 extends Int {
    public static final Int160 DEFAULT = new Int160(BigInteger.ZERO);

    public Int160(BigInteger value) {
        super(160, value);
    }

    public Int160(long value) {
        this(BigInteger.valueOf(value));
    }
}
