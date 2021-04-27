package com.provider.rpc.protocal;

import lombok.Getter;

/**
 * @author zxb
 * @date 2021-04-16 15:54
 **/
public enum MessageTypeEnum {
    REQEUST(1),RESPONSE(2);

    /**
     * 报文类型(响应或请求)
     */
    @Getter
    private int type;

    MessageTypeEnum(int type){
        this.type = type;
    }

    public static MessageTypeEnum findByType(int type){
        MessageTypeEnum[] values = MessageTypeEnum.values();
        for (MessageTypeEnum value : values) {
            if(value.type == type){
                return value;
            }
        }
        return null;
    }

}
