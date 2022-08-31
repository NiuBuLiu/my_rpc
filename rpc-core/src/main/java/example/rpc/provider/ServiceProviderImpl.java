package example.rpc.provider;

import example.rpc.enumeration.RpcError;
import example.rpc.exception.RpcException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 默认的服务注册表，保存服务端本地服务
 * 将服务名和提供服务的对象的对应关系保存在ConcurrentHashMap中，
 * 并利用一个Set来保存哪些服务已经被注册，在注册服务时，采用这个对象实现的接口的完整类名作为服务名
 * 要获取服务的对象时，直接去map中查找即可
 * @Author admin
 * @Date 2022/8/7 20:51
 * @Version 1.0
 */
public class ServiceProviderImpl implements ServiceProvider {

    private static final Logger logger = LoggerFactory.getLogger(ServiceProviderImpl.class);

    private static final Map<String, Object> serviceMap = new ConcurrentHashMap<>();
    private static final Set<String> registeredService = ConcurrentHashMap.newKeySet();

    @Override
    public synchronized <T> void addServiceProvider(T servie, String serviceName) {
        if(registeredService.contains(serviceName)) {
            return;
        }
        registeredService.add(serviceName);
        serviceMap.put(serviceName, servie);
        logger.info("向接口: {} 注册服务: {}",servie.getClass().getInterfaces(), serviceName);
    }

    @Override
    public Object getServiceProvider(String serviceName) {
        Object service = serviceMap.get(serviceName);
        if(service == null) {
            throw new RpcException(RpcError.SERVICE_NOT_FOUND);
        }
        return service;
    }
}
