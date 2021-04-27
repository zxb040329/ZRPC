package com.qf.rpc.provider.cache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zxb
 * @date 2021-04-19 23:31
 **/
public class LocalCache {

    public static Map<String,Object> map = new ConcurrentHashMap<>();



}
