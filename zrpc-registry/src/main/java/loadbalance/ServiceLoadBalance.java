package loadbalance;

import java.util.List;

/**
 * @author zxb
 * @date 2021-04-18 16:10
 **/
public interface ServiceLoadBalance<T> {

    T select(List<T> servers,int hashCode);
}
