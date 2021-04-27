package com.qf.rpc.consumer;

import core.RegistryService;
import core.RegistryTypeEnum;
import factory.RegistryServiceFactory;
import lombok.Setter;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * @author zxb
 * @date 2021-04-21 18:05
 **/
public class RpcReferenceBean implements FactoryBean<Object> {

    @Setter
    private Class<?> interfaceClass;

    @Setter
    private String registryType;

    @Setter
    private String registryAddr;

    @Setter
    private String version;

    @Setter
    private long timeout;

    @Setter
    private Object object;


    @Override
    public Object getObject() {
        return object;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    public void init() {

        //1.获取操作中心的实例对象
        final RegistryService registryService = RegistryServiceFactory.initRegistryService(registryAddr, RegistryTypeEnum.valueOf(registryType).getType());

        //2.构建代理对象
        this.object = Proxy.newProxyInstance(
                interfaceClass.getClassLoader()
                ,new Class[]{interfaceClass}
                ,new RpcInvokerProxy(registryService,version,timeout));


    }
}
