package com.provider.rpc.serialization.exception;

/**
 * @author zxb
 * @date 2021-04-17 9:16
 **/
public class RpcSerializationException extends RuntimeException {

    public RpcSerializationException(String msg){
        super(msg);
    }

    public RpcSerializationException(Throwable cause){
        super(cause);
    }
}
