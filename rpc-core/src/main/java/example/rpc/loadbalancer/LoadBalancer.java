package example.rpc.loadbalancer;

import example.rpc.entity.RpcRequest;

import java.util.List;

/**
 * 负载均衡接口
 * @Author admin
 * @Date 2022/8/12 10:28
 * @Version 1.0
 */
public interface LoadBalancer {

    /**
     * 从现有服务地址列表中选择一个
     * @return
     */
    String select(List<String> serviceUrlList, RpcRequest rpcRequest);

}
