package com.provider.rpc.serialization.impl;


import com.caucho.hessian.io.HessianSerializerInput;
import com.caucho.hessian.io.HessianSerializerOutput;
import com.provider.rpc.serialization.RpcSerialization;
import com.provider.rpc.serialization.exception.RpcSerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author zxb
 * @date 2021-04-17 9:18
 **/
public class HessianSerializationImpl implements RpcSerialization {
    @Override
    public <T> byte[] serializer(T obj) {
        if(obj == null){
            throw new NullPointerException();
        }
        byte[] bytes;
        //创建一个输出对象
        HessianSerializerOutput hessianSerializerOutput;
        //内存流 ByteArrayOutputStream 写到内存中
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream()){
            hessianSerializerOutput = new HessianSerializerOutput(stream);
            //写入对象，并将其存储到对应的内存流当中
            hessianSerializerOutput.writeObject(obj);
            hessianSerializerOutput.flush();
            //从内存流中获取到对应的字节数组
            bytes = stream.toByteArray();

        } catch (IOException e) {
            throw new RpcSerializationException(e + "编译失败");
        }

        return bytes;
    }

    @Override
    public <T> T deSerializer(byte[] bytes, Class<T> clazz) {
        if(bytes == null){
            throw new NullPointerException();
        }
        HessianSerializerInput hessianSerializerInput;
        T object;
        try(ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            hessianSerializerInput = new HessianSerializerInput(inputStream);
            object = (T) hessianSerializerInput.readObject(clazz);
        } catch (Exception e) {
            throw new RpcSerializationException(e + "反编译失败");
        }

        return  object;
    }
}
