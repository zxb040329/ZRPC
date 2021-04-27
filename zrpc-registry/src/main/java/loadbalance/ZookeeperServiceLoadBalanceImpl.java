package loadbalance;

import com.provider.rpc.core.ServiceMeta;
import org.apache.curator.x.discovery.ServiceInstance;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

/**
 * @author zxb
 * @date 2021-04-18 16:11
 **/
public class ZookeeperServiceLoadBalanceImpl implements ServiceLoadBalance<ServiceInstance<ServiceMeta>> {

    public static final int VIRTUAL_NODE_NUM = 3;

    @Override
    public ServiceInstance<ServiceMeta> select(List<ServiceInstance<ServiceMeta>> servers, int hashCode) {
        final Random random = new Random();

        //1.将服务列表通过一致性hash算法构成一个环
        TreeMap<Integer,ServiceInstance<ServiceMeta>> treeMap = new TreeMap<>();
        for (ServiceInstance<ServiceMeta> server : servers) {
            final ServiceMeta serviceMeta = server.getPayload();
            String key = buildServiceKey(serviceMeta);
            for (int i = 0; i < VIRTUAL_NODE_NUM; i++) {
                treeMap.put((key + ":" + random.nextInt(Integer.MAX_VALUE)).hashCode(),server);
            }
        }

        //2.根据客户端的hashcode值获取到对应的服务实例
        Map.Entry<Integer, ServiceInstance<ServiceMeta>> entry = treeMap.ceilingEntry(hashCode);
        if (entry == null){
            entry = treeMap.firstEntry();
        }
        return entry.getValue();
    }

    /**
     *
     * @param serviceMeta
     * @return
     */
    private String buildServiceKey(ServiceMeta serviceMeta) {
        final StringBuilder sb = new StringBuilder(serviceMeta.getServiceAddress());
        sb.append(":");
        sb.append(serviceMeta.getServicePort());
        return sb.toString();
    }
}
