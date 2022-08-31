package example.rpc.transport.socket.client;

import example.rpc.loadbalancer.LoadBalancer;
import example.rpc.loadbalancer.RandomLoadBalancer;
import example.rpc.registry.ServiceDiscovery;
import example.rpc.registry.ServiceRegistry;
import example.rpc.registry.ZookeeperServiceDiscovery;
import example.rpc.registry.ZookeeperServiceRegistry;
import example.rpc.serializer.CommonSerializer;
import example.rpc.transport.RpcClient;
import example.rpc.entity.RpcRequest;
import example.rpc.entity.RpcResponse;
import example.rpc.enumeration.ResponseCode;
import example.rpc.enumeration.RpcError;
import example.rpc.exception.RpcException;
import example.rpc.transport.socket.util.ObjectReader;
import example.rpc.transport.socket.util.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Socket方式远程方法调用的消费者（客户端）
 * @Author admin
 * @Date 2022/8/6 20:43
 * @Version 1.0
 */
public class SocketClient implements RpcClient {
    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    private final ServiceDiscovery serviceDiscovery;

    private CommonSerializer serializer;

    public SocketClient() {
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }
    public SocketClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }
    public SocketClient(Integer serializer) {
        this(serializer, new RandomLoadBalancer());
    }

    public SocketClient(Integer serializer, LoadBalancer loadBalancer) {
        this.serviceDiscovery = new ZookeeperServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializer);
    }

    @Override
    public Object sendRequest(RpcRequest rpcRequest) {
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName(), rpcRequest);
        try (Socket socket = new Socket();) {
            socket.connect(inetSocketAddress);
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();
            ObjectWriter.writeObject(outputStream, rpcRequest, serializer);
            Object obj = ObjectReader.readObject(inputStream);
            RpcResponse rpcResponse = (RpcResponse) obj;
            if (rpcResponse == null) {
                logger.error("服务调用失败，service：{}", rpcRequest.getInterfaceName());
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            if (rpcResponse.getStatusCode() == null || rpcResponse.getStatusCode() != ResponseCode.SUCCESS.getCode()) {
                logger.error("调用服务失败, service: {}, response:{}", rpcRequest.getInterfaceName(), rpcResponse);
                throw new RpcException(RpcError.SERVICE_INVOCATION_FAILURE, " service:" + rpcRequest.getInterfaceName());
            }
            return rpcResponse;
        } catch (IOException e) {
            logger.error("调用方法时发生错误", e);
            return null;
        }
    }

}
