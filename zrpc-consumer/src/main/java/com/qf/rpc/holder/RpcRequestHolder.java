package com.qf.rpc.holder;

import com.provider.rpc.core.RpcFuture;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author zxb
 * @date 2021-04-23 17:33
 **/
public class RpcRequestHolder {
    //atomicLong
    public static final AtomicLong ATOMIC_LONG = new AtomicLong(0);

    public static final Map<Long, RpcFuture> REQUEST_MAP = new ConcurrentHashMap<>();

}
