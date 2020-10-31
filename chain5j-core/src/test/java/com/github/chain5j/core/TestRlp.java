package com.github.chain5j.core;

import com.github.chain5j.bip44.HexUtils;
import com.github.chain5j.protocol.core.methods.request.Account;
import com.github.chain5j.protocol.core.methods.request.AccountInfo;
import com.github.chain5j.protocol.core.methods.request.AddressStore;
import com.github.chain5j.rlp.RlpEncoder;
import com.github.chain5j.rlp.RlpList;
import com.github.chain5j.rlp.RlpString;
import com.github.chain5j.rlp.RlpType;
import com.github.chain5j.utils.Numeric;
import org.junit.Test;

import java.util.*;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2020/6/13 15:23
 * @Copyright Copyright@2020
 */
public class TestRlp {

    @Test
    public void Rlp() {
        List<RlpType> values = new ArrayList<>();
        values.add(new RlpList());
        RlpList rlpList = new RlpList(values);
        byte[] bytes = RlpEncoder.encode(rlpList);
        System.out.println(Numeric.toHexString(bytes));
    }

    @Test
    public void TestAddressStore() {
        AddressStore addressStore = new AddressStore();
        TreeMap<String, String> map = new TreeMap<>();
        map.put("1", "1");
        map.put("3", "3");
        map.put("2", "2");
        addressStore.setKvs(map);
        byte[] rlpBytes = addressStore.toRlp();
        System.out.println(Numeric.toHexString(rlpBytes));

        Account.AccountTxData accountTxData = new Account.AccountTxData();
        accountTxData.setOperation(Account.AccountOp.RegisterAcountOp);
        accountTxData.setData(rlpBytes);
        String input = accountTxData.toRlpEncodeStr();
        System.out.println(input);

        AccountInfo accountInfo = new AccountInfo("1", "1");
        TreeMap<String, AddressStore> addresses = new TreeMap();
        addresses.put("0x51Af97344F891fAf8B2207e17944206BA530074D", new AddressStore());
        accountInfo.setAddresses(addresses);
        byte[] accountData = accountInfo.toRlpEncode();
        System.out.println(Numeric.toHexString(accountData));
    }

    @Test
    public void TestAddressRlp2() {
        AddressStore addressStore = new AddressStore();
        TreeMap<String, String> map = new TreeMap<>();
        map.put("1", "1");
        map.put("3", "3");
        map.put("2", "2");
        addressStore.setKvs(map);
        byte[] rlpBytes = addressStore.toRlp();
        System.out.println(Numeric.toHexString(rlpBytes));

        Account.AccountTxData accountTxData = new Account.AccountTxData();
        accountTxData.setOperation(Account.AccountOp.RegisterAcountOp);
        accountTxData.setData(rlpBytes);
        String input = accountTxData.toRlpEncodeStr();
        System.out.println(input);

        AccountInfo accountInfo = new AccountInfo("1", "1");
        TreeMap<String, AddressStore> addresses = new TreeMap();
        addresses.put("0x51Af97344F891fAf8B2207e17944206BA530074D", new AddressStore());
        accountInfo.setAddresses(addresses);
        byte[] accountData = accountInfo.toRlpEncode();
        System.out.println(Numeric.toHexString(accountData));
    }

    @Test
    public void TestAddressRlp() {
        String address = "0x51Af97344F891fAf8B2207e17944206BA530074D";
        RlpString rlpString = RlpString.create(Numeric.hexStringToByteArray(address));
        byte[] bytes = RlpEncoder.encode(rlpString);

        System.out.println(HexUtils.toHex(bytes));
        TreeMap<String, AddressStore> addresses = new TreeMap<>();
        addresses.put("0x51Af97344F891fAf8B2207e17944206BA530074D", new AddressStore());
        addrStore[] addresses1 = new addrStore[addresses.size()];
        addresses1[0] = new addrStore(address, new AddressStore());

        List<RlpType> result = new ArrayList<>();
        for (int i = 0; i < addresses1.length; i++) {
            result.add(addresses1[0].toRlpList());
        }
        RlpList rlpList = new RlpList(result);
        byte[] bytes2 = RlpEncoder.encode(rlpList);
        System.out.println(HexUtils.toHex(bytes2));
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
