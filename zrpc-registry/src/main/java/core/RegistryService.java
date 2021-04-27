package core;

import com.provider.rpc.core.ServiceMeta;

/**
 * @author zxb
 * @date 2021-04-18 16:01
 * 定义操作注册中心的服务接口
 **/
public interface RegistryService {

    /**
     * 注册服务
     * @param serviceMeta
     */
    void register(ServiceMeta serviceMeta) throws Exception;

    /**
     * 取消注册服务
     * @param serviceMeta
     */
    void unRegister(ServiceMeta serviceMeta) throws Exception;

    /**
     * 发现服务
     * @param serviceMark
     * @param hashCode
     * @return
     */
    ServiceMeta discoverService(String serviceMark, int hashCode) throws Exception;
}
