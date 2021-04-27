package com.provider.rpc.serialization.impl;

import com.provider.rpc.serialization.RpcSerialization;
import com.provider.rpc.serialization.exception.RpcSerializationException;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * @author zxb
 * @date 2021-04-17 15:19
 **/
public class ProtobufImpl implements RpcSerialization {
    @Override
    public <T> byte[] serializer(T obj) {
        if(obj == null){
            throw new NullPointerException();
        }
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        Class<T> clazz = (Class<T>) obj.getClass();
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
    }

    @Override
    public <T> T deSerializer(byte[] bytes, Class<T> clazz) {
        if(bytes == null){
            throw new NullPointerException();
        }
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T obj = null;
        try {
            obj = clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RpcSerializationException(e);
        } catch (IllegalAccessException e) {
            throw new RpcSerializationException(e);
        }
        ProtostuffIOUtil.mergeFrom(bytes, obj, schema);

        return obj;
    }
}
