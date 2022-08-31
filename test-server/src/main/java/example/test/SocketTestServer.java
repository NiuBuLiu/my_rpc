package example.test;

import example.rpc.api.HelloService;
import example.rpc.provider.ServiceProviderImpl;
import example.rpc.transport.socket.server.SocketServer;

/**
 * 测试用的服务提供方（服务端）
 * @Author admin
 * @Date 2022/8/6 21:34
 * @Version 1.0
 */
public class SocketTestServer {
    public static void main(String[] args) {
        HelloService helloService = new HelloServiceImpl();
        // ServiceProviderImpl serviceRegistry = new ServiceProviderImpl();
        // serviceRegistry.register(helloService);
        SocketServer socketServer = new SocketServer("127.0.0.1", 9998);
        // socketServer.publishService(helloService, HelloService.class);
    }
}
