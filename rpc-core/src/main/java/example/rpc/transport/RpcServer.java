package example.rpc.transport;

import example.rpc.serializer.CommonSerializer;

/**
 * 服务端类通用接口
 * @Author admin
 * @Date 2022/8/8 20:50
 * @Version 1.0
 */
public interface RpcServer {

    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    void start();

    <T> void publishService(T service, String serviceName);
    /*<T> void publishService(T service, Class<T> serviceClass);*/


}
