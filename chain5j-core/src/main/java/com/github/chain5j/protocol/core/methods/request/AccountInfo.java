package com.github.chain5j.protocol.core.methods.request;

import com.github.chain5j.rlp.*;
import com.google.gson.Gson;
import com.github.chain5j.rlp.*;
import com.github.chain5j.utils.Numeric;
import com.github.chain5j.utils.StringUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2020/6/12 21:36
 * @Copyright Copyright@2020
 */
public class AccountInfo {
    static Gson gson = new Gson();
    private int nonce;
    private BigInteger balance;
    private TreeMap<String, AddressStore> addresses;
    /**
     * 用户名称 common name
     */
    private String cn;
    /**
     * 所在域
     */
    private String domain;
    /**
     * 是否为管理员
     */
    private boolean isAdmin;
    /**
     * 是否允许部署合约
     */
    private boolean deployContract;
    /**
     * 管理员权限
     */
    private Permissions permissions;
    /**
     * 账户是否被冻结
     */
    private boolean isFrozen;
    /**
     * 扩展字段
     */
    private TreeMap<String, String> xxx;

    public AccountInfo() {
    }

    public AccountInfo(String cn, String domain) {
        this.cn = cn;
        this.domain = domain;
    }

    public int getNonce() {
        return nonce;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

//        public BigInteger getBalance() {
//            return Numeric.decodeQuantity(balance);
//        }
//
//        public void setBalance(String balance) {
//            this.balance = balance;
//        }

    public BigInteger getBalance() {
        return balance;
    }

    public void setBalance(BigInteger balance) {
        this.balance = balance;
    }

    public TreeMap<String, AddressStore> getAddresses() {
        return addresses;
    }

    public void setAddresses(TreeMap<String, AddressStore> addresses) {
        this.addresses = addresses;
    }

    public String getCn() {
        return cn;
    }

    public void setCn(String cn) {
        this.cn = cn;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public Boolean getDeployContract() {
        return deployContract;
    }

    public void setDeployContract(Boolean deployContract) {
        this.deployContract = deployContract;
    }

    public Permissions getPermissions() {
        return permissions;
    }

    public void setPermissions(Permissions permissions) {
        this.permissions = permissions;
    }

    public Boolean getFrozen() {
        return isFrozen;
    }

    public void setFrozen(Boolean frozen) {
        isFrozen = frozen;
    }

    public TreeMap<String, String> getXxx() {
        return xxx;
    }

    public void setXxx(TreeMap<String, String> xxx) {
        this.xxx = xxx;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    public byte[] toRlpEncode() {
        RlpType rlpList = toRlpType();
        byte[] bytes = RlpEncoder.encode(rlpList);
        return bytes;
    }

    public RlpType toRlpType() {
        List<RlpType> result = new ArrayList<>();
        result.add(RlpString.create(nonce));
        if (balance == null) {
            // 80
            result.add(RlpString.create(""));
        } else {
            result.add(RlpString.create(balance));
        }
        if (addresses == null || addresses.size() == 0) {
            // c0
            result.add(RlpList.createNil());
        } else {
            List<RlpType> addressRlpTypeList = new ArrayList<>();
            for (Map.Entry<String, AddressStore> entry : addresses.entrySet()) {
                addrStore addrS = new addrStore(entry.getKey(), entry.getValue());
                addressRlpTypeList.add(addrS.toRlpList());
            }
            RlpList rlpList1 = new RlpList(addressRlpTypeList);
            result.add(rlpList1);
        }

        result.add(RlpString.create(cn));
        result.add(RlpString.create(domain));
        result.add(RlpString.create(RlpBoolean.parse(isAdmin)));
        result.add(RlpString.create(RlpBoolean.parse(deployContract)));
        if (permissions == null) {
            result.add(RlpList.createNil());
        } else {
            result.add(permissions.toRlpType());// rlp:"nil"
        }
        result.add(RlpString.create(RlpBoolean.parse(isFrozen)));

        if (xxx == null || xxx.size() == 0) {
            result.add(RlpList.createNil());
        } else {
            List<RlpType> xxxRlpTypeList = new ArrayList<>();
            for (Map.Entry<String, String> entry : xxx.entrySet()) {
                // 将map添加为一个对象
                List<RlpType> values = new ArrayList<>();
                values.add(RlpString.create(entry.getKey()));
                values.add(RlpString.create(entry.getValue()));
                xxxRlpTypeList.add(new RlpList(values));
            }
            RlpList rlpList2 = new RlpList(xxxRlpTypeList);
            result.add(rlpList2);
        }

        return new RlpList(result);
    }

    public String toRlpEncodeStr() {
        byte[] bytes = toRlpEncode();
        return Numeric.toHexString(bytes);
    }

    public String accountName() {
        if (StringUtils.isNotEmpty(domain)) {
            return cn + "@" + domain;
        }

        return cn;
    }


    class addrStore {
        String addr;
        AddressStore store;

        public addrStore(String addr, AddressStore store) {
            this.addr = addr;
            store = store;
        }

        public String getAddr() {
            return addr;
        }

        public void setAddr(String addr) {
            this.addr = addr;
        }

        public AddressStore getStore() {
            return store;
        }

        public void setStore(AddressStore store) {
            this.store = store;
        }

        public byte[] toRlp() {
            List<RlpType> result = new ArrayList<>();
            if (addr.startsWith("0x")) {
                result.add(RlpString.create(Numeric.hexStringToByteArray(addr)));
            } else {
                result.add(RlpString.create(addr.getBytes()));
            }
            if (store == null || store.getKvs() == null) {
                result.add(RlpList.createNil());
            } else {
                result.add(RlpString.create(store.toRlp()));
            }

            RlpList rlpList = new RlpList(result);
            byte[] bytes = RlpEncoder.encode(rlpList);
            return bytes;
        }

        public RlpList toRlpList() {
            List<RlpType> result = new ArrayList<>();
            if (addr.startsWith("0x")) {
                result.add(RlpString.create(Numeric.hexStringToByteArray(addr)));
            } else {
                result.add(RlpString.create(addr.getBytes()));
            }
            if (store == null || store.getKvs() == null) {
                result.add(RlpList.createNil());
            } else {
                result.add(RlpString.create(store.toRlp()));
            }

            RlpList rlpList = new RlpList(result);
            return rlpList;
        }
    }
}
