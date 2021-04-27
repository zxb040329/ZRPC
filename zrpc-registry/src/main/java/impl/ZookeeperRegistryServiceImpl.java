package impl;

import com.provider.rpc.core.RpcUtils;
import com.provider.rpc.core.ServiceMeta;
import core.RegistryService;
import loadbalance.ZookeeperServiceLoadBalanceImpl;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.List;

/**
 * @author zxb
 * @date 2021-04-18 16:28
 **/
public class ZookeeperRegistryServiceImpl implements RegistryService {

    public static final String ZK_BASE_PATH = "zrpc";

    private ServiceDiscovery<ServiceMeta> discovery;

    public ZookeeperRegistryServiceImpl(String serviceAddress){
        final ExponentialBackoffRetry backoffRetry = new ExponentialBackoffRetry(1000, 3);
        final CuratorFramework client = CuratorFrameworkFactory.newClient(serviceAddress, backoffRetry);
        client.start();

        final JsonInstanceSerializer serializer = new JsonInstanceSerializer(ServiceMeta.class);

        discovery = ServiceDiscoveryBuilder.builder(ServiceMeta.class)
                .client(client)
                .serializer(serializer)
                .basePath(ZK_BASE_PATH)
                .build();
        try {
            discovery.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {
        final ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance.<ServiceMeta>builder()
                .name(RpcUtils.buildServiceMark(serviceMeta.getServiceName(), serviceMeta.getVersion()))
                .address(serviceMeta.getServiceAddress())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();

        discovery.registerService(serviceInstance);

    }

    @Override
    public void unRegister(ServiceMeta serviceMeta) throws Exception {
        final ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance.<ServiceMeta>builder()
                .name(RpcUtils.buildServiceMark(serviceMeta.getServiceName(), serviceMeta.getVersion()))
                .address(serviceMeta.getServiceAddress())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();

        discovery.unregisterService(serviceInstance);
    }

    @Override
    public ServiceMeta discoverService(String serviceMark, int hashCode) throws Exception {
        //1.根据serviceKey获取到对应的服务列表
        final Collection<ServiceInstance<ServiceMeta>> serviceInstances = discovery.queryForInstances(serviceMark);

        //2.根据负载均衡实现逻辑，得到对应服务节点
        final ZookeeperServiceLoadBalanceImpl zookeeperServiceLoadBalance = new ZookeeperServiceLoadBalanceImpl();
//        RegistryServiceFactory.initRegistryService(serviceMark, RegistryTypeEnum.ZOOKEEPER.getType());
        final ServiceInstance<ServiceMeta> select = zookeeperServiceLoadBalance.select((List<ServiceInstance<ServiceMeta>>) serviceInstances, hashCode);

        if(select == null){
            return null;
        }

        //3.获取服务实例保存的服务元信息
        return select.getPayload();
    }
}
