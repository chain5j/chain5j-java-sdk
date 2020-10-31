package com.github.chain5j.protocol.core.methods.request;

import com.github.chain5j.rlp.RlpEncoder;
import com.github.chain5j.rlp.RlpList;
import com.github.chain5j.rlp.RlpString;
import com.github.chain5j.rlp.RlpType;
import com.github.chain5j.utils.Numeric;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2020/6/11 23:19
 * @Copyright Copyright@2020
 */
public class Account {
    public enum AccountOp {
        RegisterAcountOp {
            @Override
            public int getValue() {
                return 0;
            }
        },
        FrozenAccountOp {
            @Override
            public int getValue() {
                return 1;
            }
        },
        UpdatePermissionOp {
            @Override
            public int getValue() {
                return 2;
            }
        },
        RegisterDomain {
            @Override
            public int getValue() {
                return 3;
            }
        },
        SetPartnerOp {
            @Override
            public int getValue() {
                return 4;
            }
        },
        LostRequestOp {
            @Override
            public int getValue() {
                return 5;
            }
        },
        FoundRequestOp {
            @Override
            public int getValue() {
                return 6;
            }
        },
        LostResetOp {
            @Override
            public int getValue() {
                return 7;
            }
        };

        public abstract int getValue();
    }

    public static class AccountTxData {
        private AccountOp operation;
        //Account   *accounts.AccountStore
        private byte[] data;

        public AccountTxData() {
        }

        public AccountTxData(AccountOp operation, byte[] data) {
            this.operation = operation;
            this.data = data;
        }

        public AccountOp getOperation() {
            return operation;
        }

        public void setOperation(AccountOp operation) {
            this.operation = operation;
        }

        public byte[] getData() {
            return data;
        }

        public void setData(byte[] data) {
            this.data = data;
        }

        public String toRlpEncodeStr() {
            byte[] bytes = toRlpEncode();
            return Numeric.toHexString(bytes);
        }

        private byte[] toRlpEncode() {
            byte[] bytes = RlpEncoder.encode(toRlpType());
            return bytes;
        }

        public RlpType toRlpType() {
            List<RlpType> result = new ArrayList<>();
            result.add(RlpString.create(operation.getValue()));
            result.add(RlpString.create(data));

            RlpList rlpList = new RlpList(result);
            return rlpList;
        }
    }
}
