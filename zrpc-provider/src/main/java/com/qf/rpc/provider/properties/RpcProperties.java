package com.qf.rpc.provider.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author zxb
 * @date 2021-04-19 22:31
 **/
@Data
@ConfigurationProperties(prefix = "zrpc")
//@Configuration
public class RpcProperties {

    /**
     * zrpc.registryType=ZOOKEEPER
     * zrpc.registryAddr=192.168.209.128:2181
     * zrpc.servicePort=8888
     */

    public String registryType;

    public String registryAddr;

    public int servicePort;


}
