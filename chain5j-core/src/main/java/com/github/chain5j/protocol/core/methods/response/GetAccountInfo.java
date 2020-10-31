package com.github.chain5j.protocol.core.methods.response;

import com.github.chain5j.protocol.core.Response;
import com.github.chain5j.protocol.core.methods.request.AccountInfo;

/**
 * @Description:
 * @Author: xwc1125
 * @Date: 2019-06-17 17:29
 * @Copyright Copyright@2019
 */
public class GetAccountInfo extends Response<AccountInfo> {
    public AccountInfo getAccountInfo() {
        return this.getResult();
    }

}
