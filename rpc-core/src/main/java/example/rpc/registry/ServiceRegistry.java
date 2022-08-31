package example.rpc.registry;

import java.net.InetSocketAddress;

/**
 * 服务注册通用接口
 * @Author admin
 * @Date 2022/8/10 10:31
 * @Version 1.0
 */
public interface ServiceRegistry {

    /**
     * 将一个服务注册进注册表中
     * @param serviceName   服务名称
     * @param inetSocketAddress 提供服务的地址
     */
    void register(String serviceName, InetSocketAddress inetSocketAddress);

}
