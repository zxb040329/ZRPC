package com.qf.rpc.provider.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zxb
 * @date 2021-04-19 22:10
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {

    Class<?> serviceInterface() default Object.class;

    String version() default "0x22";

//    @AliasFor(annotation = Component.class)
//    String serviceName() default "";

}
