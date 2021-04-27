package com.provider.rpc.serialization;

/**
 * @author zxb
 * @date 2021-04-17 9:13
 **/
public interface RpcSerialization {

    /**
     * 序列化
     * @param obj
     * @param <T>
     * @return
     */
    <T> byte[] serializer(T obj);

    /**
     * 反序列化
     * @param bytes
     * @param clazz
     * @param <T>
     * @return
     */
    <T> T deSerializer(byte[] bytes,Class<T> clazz);
}
