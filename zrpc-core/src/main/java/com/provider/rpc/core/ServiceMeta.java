package com.provider.rpc.core;

import lombok.Data;

import java.io.Serializable;

/**
 * 描述服务的元信息
 * 我们将服务发布到注册中心，用来描述当前服务状态
 * @author zxb
 * @date 2021-04-16 9:39
 **/
@Data
public class ServiceMeta implements Serializable {
    private static final long serialVersionUID = 1226732392381629573L;

    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 版本号
     */
    private String version;

    /**
     * 服务地址
     */
    private String serviceAddress;

    /**
     * 服务端口号
     */
    private int servicePort;

}
