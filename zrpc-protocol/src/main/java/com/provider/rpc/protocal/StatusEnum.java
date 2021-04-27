package com.provider.rpc.protocal;

import lombok.Getter;

/**
 * @author zxb
 * @date 2021-04-20 10:05
 **/
public enum StatusEnum {
    SUCCESSFUL(1),FAILURE(2);

    @Getter
    int type;

    StatusEnum(int type){
        this.type = type;
    }

    public static StatusEnum findByType(int type){
        for (StatusEnum value : StatusEnum.values()) {
            if(value.type == type){
                return value;
            }
        }
        return null;
    }

}
