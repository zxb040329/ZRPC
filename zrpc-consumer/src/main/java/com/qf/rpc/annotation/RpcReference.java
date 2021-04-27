package com.qf.rpc.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zxb
 * @date 2021-04-21 17:56
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Autowired
public @interface RpcReference {

    String registryType() default "ZOOKEEPER";

    String registryAddr() default "127.0.0.1:1281";

    String version() default "0x22";

    long timeout() default 3000;
}
