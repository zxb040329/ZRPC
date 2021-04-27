package com.qf.rpc.controller;

import com.qf.rpc.annotation.RpcReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import qf.rpc.UserService;

import java.util.Random;

/**
 * @author zxb
 * @date 2021-04-21 18:01
 **/
@RestController
public class UserController {


    @RpcReference(registryAddr = "192.168.209.128:2181",timeout = 5000)
    private UserService service;

    @GetMapping("/hello")
    public String hello(){
        final Random random = new Random();
        final int num = random.nextInt(100);
        System.out.println(num);
        final String hello = service.hello(num);
        System.out.println(hello);
        return hello;
    }
}

//Todo 注册地址有疑问 —— 得是注册中心所在的地址