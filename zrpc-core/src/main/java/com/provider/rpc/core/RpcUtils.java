package com.provider.rpc.core;

/**
 * @author zxb
 * @date 2021-04-16 9:39
 **/
public class RpcUtils {

    /**
     * 根据服务的名称及服务的版本号，构建出一个服务的唯一标识
     * @param serviceName
     * @param version
     * @return
     */
    public static String buildServiceMark(String serviceName,String version){
        StringBuilder sb = new StringBuilder(serviceName);
        sb.append("#");
        sb.append(version);
        return sb.toString();
    }
}
