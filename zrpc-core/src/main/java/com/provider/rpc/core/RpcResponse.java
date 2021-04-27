package com.provider.rpc.core;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zxb
 * @date 2021-04-16 9:39
 **/
@Data
public class RpcResponse implements Serializable {
    private static final long serialVersionUID = -2237654908303857418L;

    /**
     * 成功时返回data数据
     */
    private Object data;

    /**
     * 失败时返回错误信息
     */
    private String msg;

}
