package example.rpc.registry;

import example.rpc.entity.RpcRequest;
import example.rpc.loadbalancer.LoadBalancer;
import example.rpc.loadbalancer.RandomLoadBalancer;
import example.rpc.registry.util.CuratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author admin
 * @Date 2022/8/11 21:46
 * @Version 1.0
 */
public class ZookeeperServiceDiscovery implements ServiceDiscovery{

    private static final Logger logger = LoggerFactory.getLogger(ZookeeperServiceDiscovery.class);

    private final LoadBalancer loadBalancer;

    public ZookeeperServiceDiscovery(LoadBalancer loadBalancer) {
        if (loadBalancer == null) {
            this.loadBalancer = new RandomLoadBalancer();
        } else {
            this.loadBalancer = loadBalancer;
        }
    }

    @Override
    public InetSocketAddress lookupService(String serviceName, RpcRequest rpcRequest) {
        List<String> childrenNodes = CuratorUtils.getChildrenNodes(CuratorUtils.getZkClient(), serviceName);
        if (!childrenNodes.isEmpty()) {
            // String targetServiceUrl = childrenNodes.get(0);
            String targetServiceUrl = loadBalancer.select(childrenNodes, rpcRequest);
            logger.info("成功找到服务地址:[{}]", targetServiceUrl);
            String[] socketAddressArray = targetServiceUrl.split(":");
            String host = socketAddressArray[0];
            int port = Integer.parseInt(socketAddressArray[1]);
            return new InetSocketAddress(host, port);
        }
        return null;
    }
}
