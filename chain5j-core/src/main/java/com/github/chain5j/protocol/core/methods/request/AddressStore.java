package com.github.chain5j.protocol.core.methods.request;

import com.google.gson.Gson;
import com.github.chain5j.rlp.RlpEncoder;
import com.github.chain5j.rlp.RlpList;
import com.github.chain5j.rlp.RlpString;
import com.github.chain5j.rlp.RlpType;
import com.github.chain5j.utils.Numeric;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2020/6/12 21:34
 * @Copyright Copyright@2020
 */
public class AddressStore {
    static Gson gson = new Gson();
    private TreeMap<String, String> kvs;

    public AddressStore() {
    }

    public Map<String, String> getKvs() {
        return kvs;
    }

    public void setKvs(TreeMap<String, String> kvs) {
        this.kvs = kvs;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    public String toRlpStr() {
        byte[] bytes = toRlp();
        return Numeric.toHexString(bytes);
    }

    public byte[] toRlp() {
        List<RlpType> result = new ArrayList<>();
        for (Map.Entry<String, String> entry : kvs.entrySet()) {
            // 将map添加为一个对象
            List<RlpType> values = new ArrayList<>();
            String address = entry.getKey();
            if (address.startsWith("0x")) {
                result.add(RlpString.create(Numeric.hexStringToByteArray(address)));
            } else {
                result.add(RlpString.create(address.getBytes()));
            }
            values.add(RlpString.create(entry.getValue()));
            result.add(new RlpList(values));
        }

        RlpList rlpList = new RlpList(result);
        byte[] bytes = RlpEncoder.encode(rlpList);
        return bytes;
    }
}
