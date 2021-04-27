package com.provider.rpc.protocal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zxb
 * @date 2021-04-16 17:18
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcProtocol<T> {
    /**
     * 协议头
     */
    private MessageHeader messageHeader;

    /**
     * 协议体
     */
    private T object;

}
