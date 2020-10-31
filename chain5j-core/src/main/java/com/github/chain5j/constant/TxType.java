package com.github.chain5j.constant;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2020/3/4 15:21
 * @Copyright Copyright@2020
 */
public enum TxType {
    STATE {
        @Override
        public String getValue() {
            return "STATE";
        }
    },
    UTXO {
        @Override
        public String getValue() {
            return "UTXO";
        }
    };

    public abstract String getValue();
}
