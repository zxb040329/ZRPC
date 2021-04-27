package com.provider.rpc.protocal;

import lombok.Getter;

/**
 * @author zxb
 * @date 2021-04-16 15:49
 **/
public enum RpcSerializationTypeEnum {
    HESSION(1), PROTOBUF(2);

    /**
     * 序列化类型
     */
    @Getter
    private int type;

    RpcSerializationTypeEnum(int type) {
        this.type = type;
    }

    public static RpcSerializationTypeEnum findByType(int type){
        RpcSerializationTypeEnum[] values = RpcSerializationTypeEnum.values();
        for (RpcSerializationTypeEnum value : values) {
            if(value.type == type){
                return value;
            }
        }


        /**
         * 默认实现
         */
        return PROTOBUF;
    }

}
