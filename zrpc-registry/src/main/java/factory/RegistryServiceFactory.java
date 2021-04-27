package factory;

import core.RegistryService;
import core.RegistryTypeEnum;
import impl.EurekaRegistryServiceImpl;
import impl.ZookeeperRegistryServiceImpl;

/**
 * @author zxb
 * @date 2021-04-18 17:13
 **/
public class RegistryServiceFactory {

    private static volatile RegistryService registryService;


    public static RegistryService initRegistryService(String serviceAddress, int registryType) {
        if (registryService == null) {
            synchronized (RegistryServiceFactory.class) {
                if (registryService == null) {
                    final RegistryTypeEnum byType = RegistryTypeEnum.findByType(registryType);
                    switch (byType) {
                        case ZOOKEEPER:
                            return new ZookeeperRegistryServiceImpl(serviceAddress);
                        case EUREKA:
                            return new EurekaRegistryServiceImpl(serviceAddress);
                        default:
                            throw new IllegalArgumentException("参数异常");
                    }
                }
            }
        }
        return registryService;

    }
}
