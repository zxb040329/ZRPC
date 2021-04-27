package com.provider.rpc.core;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zxb
 * @date 2021-04-16 9:38
 **/
@Data
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = -4636586817474044025L;

    /**
     * 类名
     */
    private String className;

    /**
     * 方法名
     */
    private String methodName;

    /**
     * Todo 差参数
     * 参数类型[]
     * 参数值[]
     */

    /**
     * 参数类型数组
     */
    private Class<?>[] parameterTypes;

    /**
     * 参数值
     */
    private Object[] params;

    /**
     * 版本号
     */
    private String version;


}
