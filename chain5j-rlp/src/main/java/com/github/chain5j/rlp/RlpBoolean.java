package com.github.chain5j.rlp;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2020/6/13 17:24
 * @Copyright Copyright@2020
 */
public class RlpBoolean {

    public static final byte[] TRUE = new byte[]{1};
    private static final byte[] FALSE = new byte[]{};

    private RlpBoolean() {
    }

    public static byte[] parse(boolean value) {
        byte[] b;
        if (value) {
            b = TRUE;
        } else {
            b = FALSE;
        }
        return b;
    }
}
