package example.rpc.registry;

import example.rpc.entity.RpcRequest;

import java.net.InetSocketAddress;

/**
 * 服务发现接口
 * @Author admin
 * @Date 2022/8/11 21:44
 * @Version 1.0
 */
public interface ServiceDiscovery {
    /**
     *
     * 根据服务名称查找到地址
     * @param serviceName 服务名称
     * @return  服务实体
     */
    InetSocketAddress lookupService(String serviceName, RpcRequest rpcRequest);
}
