package example.rpc.registry.util;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * Curator(zookeeper client) utils
 * @Author admin
 * @Date 2022/8/10 11:20
 * @Version 1.0
 */
public final class CuratorUtils {

    private static final Logger logger = LoggerFactory.getLogger(CuratorUtils.class);

    private static final int BASE_SLEEP_TIME = 1000;
    private static final int MAX_RETRIES = 3;
    public static final String ZK_REGISTER_ROOT_PATH = "/my-rpc";
    private static final Map<String, List<String>> SERVICE_ADDRESS_MAP = new ConcurrentHashMap<>();
    private static final Set<String> REGISTERED_PATH_SET = ConcurrentHashMap.newKeySet();
    private static CuratorFramework zkClient;
    private static final String DEFAULT_ZOOKEEPER_ADDRESS = "localhost:2181";
    private static InetSocketAddress address;

    private CuratorUtils() {
    }

    /**
     * Create persistent nodes. Unlike temporary nodes, persistent nodes are not removed when the client disconnects
     *
     */
    public static void createPersistentNode(CuratorFramework zkClient, String serviceName, InetSocketAddress inetSocketAddress) {
        String path = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + serviceName + inetSocketAddress.toString();
        try {
            if(REGISTERED_PATH_SET.contains(path) || zkClient.checkExists().forPath(path) != null) {
                logger.info("The node already exists. The node is:[{}]", path);
            } else {
                // /my-rpc/example.rpc.api.HelloService/127.0.0.1:9999
                zkClient.create().creatingParentContainersIfNeeded().withMode(CreateMode.PERSISTENT).forPath(path);
                CuratorUtils.address = inetSocketAddress;
                logger.info("The node was created successfully. The node is:[{}]", path);
            }
        } catch (Exception e) {
            logger.error("create persistent node for path [{}] fail", path);
        }
    }

    /**
     * Gets the children under a node
     *
     * @param rpcServiceName
     * @return All child nodes under the specified node
     */
    public static List<String> getChildrenNodes(CuratorFramework zkClient, String rpcServiceName) {
        if (SERVICE_ADDRESS_MAP.containsKey(rpcServiceName)) {
            return SERVICE_ADDRESS_MAP.get(rpcServiceName);
        }
        List<String> result = null;
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        try {
            result = zkClient.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, result);
            registerWatcher(rpcServiceName, zkClient);
        } catch (Exception e) {
            logger.error("get children nodes for path [{}] fail", servicePath);
        }
        return result;
    }

    /**
     * Empty the registry of data
     */
    public static void clearRegistry() {
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        InetSocketAddress inetSocketAddress = address;
        REGISTERED_PATH_SET.stream().parallel().forEach(p -> {
            if (p.endsWith(inetSocketAddress.toString())) {
                try {
                    zkClient.delete().forPath(p);
                } catch (Exception e) {
                    logger.error("clear registry for path [{}] fail", p);
                }
            }
        });
    }

    public static CuratorFramework getZkClient() {
        // if zkClient has been started, return directly
        if (zkClient != null && zkClient.getState() == CuratorFrameworkState.STARTED) {
            return zkClient;
        }
        // Retry strategy. Retry 3 times, and will increase the sleep time between retries.
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(BASE_SLEEP_TIME, MAX_RETRIES);
        zkClient = CuratorFrameworkFactory.builder()
                // the server to connect to (can be a server list)
                .connectString(DEFAULT_ZOOKEEPER_ADDRESS)
                .retryPolicy(retryPolicy)
                .build();
        zkClient.start();
        try {
            // wait 30s until connect to the zookeeper
            if (!zkClient.blockUntilConnected(30, TimeUnit.SECONDS)) {
                throw new RuntimeException("Time out waiting to connect to ZK!");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return zkClient;
    }

    /**
     * Registers to listen for changes to the specified node
     *
     * @param rpcServiceName
     */
    private static void registerWatcher(String rpcServiceName, CuratorFramework zkClient) throws Exception {
        String servicePath = ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName;
        PathChildrenCache pathChildrenCache = new PathChildrenCache(zkClient, servicePath, true);
        PathChildrenCacheListener pathChildrenCacheListener = ((curatorFramework, pathChildrenCacheEvent) -> {
            List<String> serviceAddresses = curatorFramework.getChildren().forPath(servicePath);
            SERVICE_ADDRESS_MAP.put(rpcServiceName, serviceAddresses);
        });
        pathChildrenCache.getListenable().addListener(pathChildrenCacheListener);
        pathChildrenCache.start();
    }

}
