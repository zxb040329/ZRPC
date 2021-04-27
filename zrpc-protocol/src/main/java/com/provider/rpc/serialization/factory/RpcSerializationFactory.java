package com.provider.rpc.serialization.factory;

import com.provider.rpc.protocal.RpcSerializationTypeEnum;
import com.provider.rpc.serialization.RpcSerialization;
import com.provider.rpc.serialization.impl.HessianSerializationImpl;
import com.provider.rpc.serialization.impl.ProtobufImpl;

/**
 * @author zxb
 * @date 2021-04-17 15:58
 **/
public class RpcSerializationFactory {

    public static RpcSerialization init(int serializationType) {

        RpcSerializationTypeEnum byType = RpcSerializationTypeEnum.findByType(serializationType);
        switch (byType) {
            case HESSION:
                return new HessianSerializationImpl();
            case PROTOBUF:
                return new ProtobufImpl();
            default:
                throw new IllegalArgumentException("Serialization Type illegal:" + serializationType);
        }

    }
}
