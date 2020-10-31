package com.github.chain5j.protocol.core.methods.request;

import com.github.chain5j.rlp.*;
import com.google.gson.Gson;
import com.github.chain5j.rlp.*;
import com.github.chain5j.utils.Numeric;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2020/6/12 21:36
 * @Copyright Copyright@2020
 */
public class Permissions {
    static Gson gson = new Gson();
    /**
     * 是否允许注册用户
     */
    private Boolean registerUser;
    /**
     * 是否允许更新用户权限
     */
    private Boolean updateUser;
    /**
     * 是否允许冻结用户
     */
    private Boolean frozenUser;
    /**
     * 是否允许建立新的域名
     */
    private Boolean registerDomain;
    /**
     * 是否允许建立子域
     */
    private Boolean registerSubDomain;

    public Permissions() {
    }

    public Boolean getRegisterUser() {
        return registerUser;
    }

    public void setRegisterUser(Boolean registerUser) {
        this.registerUser = registerUser;
    }

    public Boolean getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Boolean updateUser) {
        this.updateUser = updateUser;
    }

    public Boolean getFrozenUser() {
        return frozenUser;
    }

    public void setFrozenUser(Boolean frozenUser) {
        this.frozenUser = frozenUser;
    }

    public Boolean getRegisterDomain() {
        return registerDomain;
    }

    public void setRegisterDomain(Boolean registerDomain) {
        this.registerDomain = registerDomain;
    }

    public Boolean getRegisterSubDomain() {
        return registerSubDomain;
    }

    public void setRegisterSubDomain(Boolean registerSubDomain) {
        this.registerSubDomain = registerSubDomain;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }

    public byte[] toRlpEncode() {
        RlpType rlpType = toRlpType();
        byte[] bytes = RlpEncoder.encode(rlpType);
        return bytes;
    }

    public RlpType toRlpType() {
        List<RlpType> values = new ArrayList<>();
        values.add(RlpString.create(RlpBoolean.parse(registerUser)));
        values.add(RlpString.create(RlpBoolean.parse(updateUser)));
        values.add(RlpString.create(RlpBoolean.parse(frozenUser)));
        values.add(RlpString.create(RlpBoolean.parse(registerDomain)));
        values.add(RlpString.create(RlpBoolean.parse(registerSubDomain)));
        RlpList rlpList = new RlpList(values);
        return rlpList;
    }

    public String toRlpEncodeStr() {
        byte[] bytes = toRlpEncode();
        return Numeric.toHexString(bytes);
    }
}
