package example.rpc.loadbalancer;

import example.rpc.entity.RpcRequest;

import java.util.List;

/**
 * 负载均衡轮询算法
 * @Author admin
 * @Date 2022/8/12 10:41
 * @Version 1.0
 */
public class RoundRobinLoadBalancer implements LoadBalancer {

    private int index = 0;

    @Override
    public String select(List<String> serviceUrlList, RpcRequest rpcRequest) {
        if(index >= serviceUrlList.size()) {
            index %= serviceUrlList.size();
        }
        return serviceUrlList.get(index++);
    }
}
