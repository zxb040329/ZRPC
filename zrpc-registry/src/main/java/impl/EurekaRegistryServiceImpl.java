package impl;

import com.provider.rpc.core.ServiceMeta;
import core.RegistryService;

/**
 * @author zxb
 * @date 2021-04-18 17:17
 **/
public class EurekaRegistryServiceImpl implements RegistryService {
    public EurekaRegistryServiceImpl(String serviceAddress) {
    }

    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {

    }

    @Override
    public void unRegister(ServiceMeta serviceMeta) throws Exception {

    }

    @Override
    public ServiceMeta discoverService(String serviceMark, int hashCode) throws Exception {
        return null;
    }
}
