package com.github.chain5j.abi.datatypes.generated;

import java.util.List;
import com.github.chain5j.abi.datatypes.StaticArray;
import com.github.chain5j.abi.datatypes.Type;

/**
 * Auto generated code.
 * <p><strong>Do not modifiy!</strong>
 * <p>Please use com.github.chain5j.codegen.AbiTypesGenerator in the
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 */
public class StaticArray31<T extends Type> extends StaticArray<T> {
    @Deprecated
    public StaticArray31(List<T> values) {
        super(31, values);
    }

    @Deprecated
    @SafeVarargs
    public StaticArray31(T... values) {
        super(31, values);
    }

    public StaticArray31(Class<T> type, List<T> values) {
        super(type, 31, values);
    }

    @SafeVarargs
    public StaticArray31(Class<T> type, T... values) {
        super(type, 31, values);
    }
}
