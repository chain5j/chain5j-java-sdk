package com.github.chain5j.protocol.core.methods.request;

import java.util.Arrays;
import java.util.List;

import com.github.chain5j.protocol.core.DefaultBlockParameter;

public class RpcFilter extends Filter<RpcFilter> {
    private DefaultBlockParameter fromBlock;  // optional, params - defaults to latest for both
    private DefaultBlockParameter toBlock;
    private List<String> address;  // spec. implies this can be single address as string or list

    public RpcFilter() {
        super();
    }

    public RpcFilter(DefaultBlockParameter fromBlock, DefaultBlockParameter toBlock,
                     List<String> address) {
        super();
        this.fromBlock = fromBlock;
        this.toBlock = toBlock;
        this.address = address;
    }

    public RpcFilter(DefaultBlockParameter fromBlock, DefaultBlockParameter toBlock,
                     String address) {
        this(fromBlock, toBlock, Arrays.asList(address));
    }

    public DefaultBlockParameter getFromBlock() {
        return fromBlock;
    }

    public DefaultBlockParameter getToBlock() {
        return toBlock;
    }

    public Object getAddress() {
        if (address != null && address.size() == 1) {
            return address.get(0);
        }
        return address;
    }

    @Override
    RpcFilter getThis() {
        return this;
    }
}
