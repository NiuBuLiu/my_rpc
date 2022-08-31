package example.rpc.loadbalancer;

import example.rpc.entity.RpcRequest;

import java.util.List;
import java.util.Random;

/**
 * 负载均衡随机算法
 * @Author admin
 * @Date 2022/8/12 10:38
 * @Version 1.0
 */
public class RandomLoadBalancer implements LoadBalancer{

    @Override
    public String select(List<String> serviceUrlList, RpcRequest rpcRequest) {
        return serviceUrlList.get(new Random().nextInt(serviceUrlList.size()));
    }
}
