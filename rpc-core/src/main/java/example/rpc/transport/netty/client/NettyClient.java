package example.rpc.transport.netty.client;

import example.rpc.enumeration.RpcError;
import example.rpc.exception.RpcException;
import example.rpc.factory.SingletonFactory;
import example.rpc.loadbalancer.LoadBalancer;
import example.rpc.loadbalancer.RandomLoadBalancer;
import example.rpc.registry.ServiceDiscovery;
import example.rpc.registry.ZookeeperServiceDiscovery;
import example.rpc.serializer.CommonSerializer;
import example.rpc.transport.RpcClient;
import example.rpc.entity.RpcRequest;
import example.rpc.entity.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

/**
 * NIO方式消费测客户端类
 * @Author admin
 * @Date 2022/8/8 23:06
 * @Version 1.0
 */
public class NettyClient implements RpcClient {

    private static final Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private static final Bootstrap bootstrap;
    private static final EventLoopGroup group;
    private final ServiceDiscovery serviceDiscovery;
    private CommonSerializer serializer;
    private final UnprocessedRequests unprocessedRequests;

    // 配置好Netty客户端，等待发送数据时启动，这儿是非阻塞的，是通过AttributeKey来阻塞获取返回结果
    static {
        group = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true);
    }

    public NettyClient() {
        this(DEFAULT_SERIALIZER, new RandomLoadBalancer());
    }
    public NettyClient(LoadBalancer loadBalancer) {
        this(DEFAULT_SERIALIZER, loadBalancer);
    }
    public NettyClient(Integer serializer) {
        this(serializer, new RandomLoadBalancer());
    }

    public NettyClient(Integer serializer, LoadBalancer loadBalancer) {
        this.serviceDiscovery = new ZookeeperServiceDiscovery(loadBalancer);
        this.serializer = CommonSerializer.getByCode(serializer);
        this.unprocessedRequests = SingletonFactory.getInstance(UnprocessedRequests.class);
    }

    @Override
    public CompletableFuture<RpcResponse> sendRequest(RpcRequest rpcRequest) {
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        CompletableFuture<RpcResponse> resultFutrue = new CompletableFuture<>();
        InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName(), rpcRequest);
        Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
        if (!channel.isActive()) {
            group.shutdownGracefully();
            return null;
        }
        unprocessedRequests.put(rpcRequest.getRequestId(), resultFutrue);
        channel.writeAndFlush(rpcRequest).addListener((ChannelFutureListener) future1 -> {
            if (future1.isSuccess()) {
                logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
            } else {
                future1.channel().close();
                resultFutrue.completeExceptionally(future1.cause());
                logger.error("发送消息时有错误发生: ", future1.cause());
            }
        });
        return resultFutrue;
    }


//    @Override
//    public Object sendRequest(RpcRequest rpcRequest) {
//        if(serializer == null) {
//            logger.error("未设置序列化器");
//            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
//        }
//        AtomicReference<Object> result = new AtomicReference<>(null);
//        try {
//            InetSocketAddress inetSocketAddress = serviceDiscovery.lookupService(rpcRequest.getInterfaceName(), rpcRequest);
//            Channel channel = ChannelProvider.get(inetSocketAddress, serializer);
//            if (channel.isActive()) {
//                channel.writeAndFlush(rpcRequest).addListener(future1 -> {
//                   if (future1.isSuccess()) {
//                       logger.info(String.format("客户端发送消息: %s", rpcRequest.toString()));
//                   } else {
//                       logger.error("发送消息时有错误发生: ", future1.cause());
//                   }
//                });
//                channel.closeFuture().sync();
//                // 通过AttributeKey阻塞获得返回结果，通过这种方式获取全局可见的返回结果
//                // 在获取到返回结果RpcResponse后，将这个对象作为key放入ChannelHandlerContext中
//                // 这样就可以立刻获取到结果并返回，可以在NettyClientHanler中看到放入过程
//                AttributeKey<RpcResponse> key = AttributeKey.valueOf("rpcResponse" + rpcRequest.getRequestId());
//                RpcResponse rpcResponse = channel.attr(key).get();
//                RpcMessageChecker.check(rpcRequest, rpcResponse);
//                result.set(rpcResponse.getData());
//            } else {
//                System.exit(0);
//            }
//        } catch (InterruptedException e) {
//            logger.error("发送消息时有错误发生: ", e);
//        }
//        return result.get();
//    }

}
