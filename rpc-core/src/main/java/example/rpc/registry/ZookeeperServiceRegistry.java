package example.rpc.registry;

import example.rpc.registry.util.CuratorUtils;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author admin
 * @Date 2022/8/10 10:39
 * @Version 1.0
 */
public class ZookeeperServiceRegistry implements ServiceRegistry{
    private static final Logger logger = LoggerFactory.getLogger(ZookeeperServiceRegistry.class);

    private static final CuratorFramework zkClient;

    static {
        zkClient = CuratorUtils.getZkClient();
    }

    @Override
    public void register(String serviceName, InetSocketAddress inetSocketAddress) {
        CuratorUtils.createPersistentNode(zkClient, serviceName, inetSocketAddress);
    }


}
