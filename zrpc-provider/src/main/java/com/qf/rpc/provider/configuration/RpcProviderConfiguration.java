package com.qf.rpc.provider.configuration;

import com.qf.rpc.provider.properties.RpcProperties;
import com.qf.rpc.provider.provider.RpcProvider;
import core.RegistryService;
import core.RegistryTypeEnum;
import factory.RegistryServiceFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author zxb
 * @date 2021-04-19 22:39
 **/
@Configuration
@EnableConfigurationProperties(RpcProperties.class)
public class RpcProviderConfiguration {

    @Resource
    private RpcProperties rpcProperties;

    @Bean
    public RpcProvider initRpcProvider(){
        //获得注册中心实例
        final RegistryTypeEnum registryTypeEnum = RegistryTypeEnum.valueOf(rpcProperties.getRegistryType());
        final int type = registryTypeEnum.getType();
        final RegistryService registryService = RegistryServiceFactory.initRegistryService(rpcProperties.getRegistryAddr(), type);

        return new RpcProvider(registryService,rpcProperties.getServicePort());
    }
}
