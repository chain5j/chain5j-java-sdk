package com.github.chain5j.crypto;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2020/3/4 15:15
 * @Copyright Copyright@2020
 */
public enum Interpreter {
    BASE {
        @Override
        public String getValue() {
            return "chain5j.base";
        }
    },
    ACCOUNT {
        @Override
        public String getValue() {
            return "chain5j.account";
        }
    },
    LOST {
        @Override
        public String getValue() {
            return "chain5j.lost";
        }
    },
    EVM {
        @Override
        public String getValue() {
            return "chain5j.evm";
        }
    },
    CA {
        @Override
        public String getValue() {
            return "chain5j.ca";
        }
    },
    POE {
        @Override
        public String getValue() {
            return "chain5j.poe";
        }
    },
    ETHEREUM {
        @Override
        public String getValue() {
            return "chain5j.ethereum";
        }
    };

    public abstract String getValue();

    public static Interpreter Parse(String value) {
        if (BASE.getValue().endsWith(value)) {
            return BASE;
        }
        if (ACCOUNT.getValue().endsWith(value)) {
            return ACCOUNT;
        }
        if (LOST.getValue().endsWith(value)) {
            return LOST;
        }
        if (EVM.getValue().endsWith(value)) {
            return EVM;
        }
        if (CA.getValue().endsWith(value)) {
            return CA;
        }
        if (POE.getValue().endsWith(value)) {
            return POE;
        }
        if (ETHEREUM.getValue().endsWith(value)) {
            return ETHEREUM;
        }
        return BASE;
    }
}
