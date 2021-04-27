package com.qf.rpc.provider.service;

import com.provider.rpc.core.RpcFuture;
import com.qf.rpc.provider.annotation.RpcService;
import qf.rpc.UserService;

/**
 * @author zxb
 * @date 2021-04-19 22:13
 **/
//@RpcService
//@RpcService(serviceInterface = UserServiceImpl.class)
@RpcService(serviceInterface = UserService.class)
public class UserServiceImpl implements UserService {

    @Override
    public String hello(int num) {
        return "hello,world！！！";
    }
}
