package example.test;

import example.rpc.serializer.HessianSerializer;
import example.rpc.serializer.JsonSerializer;
import example.rpc.serializer.ProtobufSerializer;
import example.rpc.transport.RpcClientProxy;
import example.rpc.api.HelloObject;
import example.rpc.api.HelloService;
import example.rpc.transport.netty.client.NettyClient;

/**
 * 测试用Netty服务消费者（客户端）
 * @Author admin
 * @Date 2022/8/9 10:38
 * @Version 1.0
 */
public class NettyTestClient {

    public static void main(String[] args) {
        NettyClient client = new NettyClient();
        // client.setSerializer(new ProtobufSerializer());
        RpcClientProxy proxy = new RpcClientProxy(client);
        HelloService helloService = proxy.getProxy(HelloService.class);
        HelloObject helloObject = new HelloObject(12, "this is message");
        String res = helloService.hello(helloObject);
        System.out.println(res);
    }
}
