package com.qf.rpc.provider.processor;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author zxb
 * @date 2021-04-20 9:38
 **/
public class BusinessThreadPool {

    private static volatile ThreadPoolExecutor executor;

    public static ThreadPoolExecutor getThreadPoolExecutor(){
        if(executor == null){
            synchronized (BusinessThreadPool.class){
                if(executor == null){
                    final int num = Runtime.getRuntime().availableProcessors();
                    executor = new ThreadPoolExecutor(num,num*2,10, TimeUnit.SECONDS,new LinkedBlockingDeque<>(100));
                }
            }
        }
        return executor;

    }


}
