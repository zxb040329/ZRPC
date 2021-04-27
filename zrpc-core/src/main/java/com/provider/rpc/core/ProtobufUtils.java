package com.provider.rpc.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zxb
 * @date 2021-04-17 18:31
 **/
public class ProtobufUtils {
    private static ConcurrentHashMap<Class<?>, Schema<?>> map = new ConcurrentHashMap<>();

    public static <T> byte[] serializer(T obj) {
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Class<T> aClass = (Class<T>) obj.getClass();
            Schema<T> schema = getSchema(aClass);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
    }

    private static <T> Schema<T> getSchema(Class<T> clazz) {

        Schema schema = map.get(clazz);
        if (schema == null) {
            schema = RuntimeSchema.getSchema(clazz);
            //put操作有线程安全问题，因为schma有可能再次变为null,—— 不会有这问题（局部变量）
            if (schema != null) {
                map.put(clazz, schema);
            }
        }
        return schema;
    }


    public static <T> T deserializer(byte[] data, Class<T> clazz) throws IllegalAccessException, InstantiationException {
        Schema<T> schema = getSchema(clazz);
        T obj = clazz.newInstance();
        ProtostuffIOUtil.mergeFrom(data, obj, schema);
        return obj;
    }


    /**
     * 对象转ByteBuf
     * @param obj
     * @return
     */
    public static ByteBuf objConvertByteBuf(Object obj){
        byte[] serializer = serializer(obj);
        ByteBuf byteBuf = Unpooled.copiedBuffer(serializer);
        return byteBuf;
    }

    /**
     * ByteBuf转对象
     * @param byteBuf
     * @param clazz
     * @param <T>
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    public static <T> T byteBufConvertObj(ByteBuf byteBuf,Class<T> clazz) throws InstantiationException, IllegalAccessException {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return deserializer(bytes, clazz);
    }



}
