package com.provider.rpc.core;

import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Promise;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zxb
 * @date 2021-04-16 9:39
 **/
@Data
public class RpcFuture<T> implements Serializable {
    private static final long serialVersionUID = 649830785715078485L;

    /**
     * 接收异步调用的结果
     */
    private Promise<T> promise;

    /**
     * 设置异步调用的时间
     */
    private long timeout;


    //Todo 后面再加的构造方法？
    public RpcFuture(DefaultPromise<T> promise, long timeout) {
        this.promise =  promise;
        this.timeout = timeout;

    }
}
