package example.rpc.provider;

/**
 * 保存和提供服务实例对象
 * @Author admin
 * @Date 2022/8/7 20:46
 * @Version 1.0
 */
public interface ServiceProvider {

    /**
     * 增加一个服务提供者
     * @param servie 服务实体
     * @param <T>  服务实体类
     */
    <T> void addServiceProvider(T servie, String serviceName);

    /**
     * 通过服务名称提供服务实体
     * @param serviceName  服务名称
     * @return  服务实体
     */
    Object getServiceProvider(String serviceName);
}
