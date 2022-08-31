package example.test;

import example.rpc.api.HelloObject;
import example.rpc.api.HelloService;
import example.rpc.transport.RpcClientProxy;
import example.rpc.transport.socket.client.SocketClient;

/**
 * 测试用的消费者（客户端）
 * @Author admin
 * @Date 2022/8/6 21:43
 * @Version 1.0
 */
public class SocketTsetClient {
    public static void main(String[] args) {
        SocketClient client = new SocketClient();
        RpcClientProxy clientProxy = new RpcClientProxy(client);
        HelloService helloService = clientProxy.getProxy(HelloService.class);
        HelloObject object = new HelloObject(12, "this is message");
        String res = helloService.hello(object);
        System.out.println(res);
    }
}
