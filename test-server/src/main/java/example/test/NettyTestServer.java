package example.test;

import example.rpc.api.HelloService;
import example.rpc.serializer.HessianSerializer;
import example.rpc.serializer.JsonSerializer;
import example.rpc.serializer.ProtobufSerializer;
import example.rpc.transport.netty.server.NettyServer;
import example.rpc.provider.ServiceProviderImpl;

/**
 * 测试用Netty服务提供者（服务端）
 * @Author admin
 * @Date 2022/8/9 10:40
 * @Version 1.0
 */
public class NettyTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        // ServiceProviderImpl registry = new ServiceProviderImpl();
        // registry.register(helloService);
        NettyServer nettyServer = new NettyServer("127.0.0.1", 9999);
//        nettyServer.setSerializer(new ProtobufSerializer());
        // nettyServer.publishService(helloService, HelloService.class);
    }
}
