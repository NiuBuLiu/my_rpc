package example.rpc.transport.netty.server;

import example.rpc.enumeration.RpcError;
import example.rpc.exception.RpcException;
import example.rpc.hook.ShutdownHook;
import example.rpc.provider.ServiceProvider;
import example.rpc.provider.ServiceProviderImpl;
import example.rpc.registry.ServiceRegistry;
import example.rpc.registry.ZookeeperServiceRegistry;
import example.rpc.serializer.CommonSerializer;
import example.rpc.transport.AbstractRpcServer;
import example.rpc.transport.RpcServer;
import example.rpc.codec.CommonDecoder;
import example.rpc.codec.CommonEncoder;
import example.rpc.serializer.KryoSerializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * NIO方式服务提供方
 * @Author admin
 * @Date 2022/8/8 21:24
 * @Version 1.0
 */
public class NettyServer extends AbstractRpcServer {

    private static final Logger logger = LoggerFactory.getLogger(NettyServer.class);

    private final String host;
    private final int port;

    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;

    private CommonSerializer serializer;

    public NettyServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public NettyServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        serviceRegistry = new ZookeeperServiceRegistry();
        serviceProvider = new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(serializer);
        scanService();
    }

    /*@Override
    public <T> void publishService(T service, Class<T> serviceClass) {
        if (serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service, serviceClass);
        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        start();
    }*/

    /**
     * 在该启动方法中配置了ServerBootStrap启动程序
     */
    @Override
    public void start() {

        ShutdownHook.getShutdownHook().addClearAllHook();

        // accept线程池，负责接收新进来的连接，然后将连接注册到工作线程当中去
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // 工作线程池，负责channel业务数据的读、写
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    // 设置非阻塞，用来建立新的accept的连接，用于构建ServerSocketChannel的工厂类
                    .channel(NioServerSocketChannel.class)
                    // serverSocketChannel上的handler
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 临时存放已完成三次握手的请求的队列的最大长度
                    // 如果未设置，或者设置的值小于，Java默认为50
                    // 如果大于队列的最大长度，请求就会被拒绝
                    // 对应tcp/ip协议的listen函数中的backlog函数，函数listen(int socketfd,int backlog)用来初始化服务端可连接队列
                    // 服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接，多个客户端来的时候，
                    // 服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小
                    .option(ChannelOption.SO_BACKLOG, 256)
                    // 开启心跳机制，在双方TCP建立连接后，且在两小时内没有进行任何数据传输
                    // 心跳机制就会被激活，TCP会自动发送一个活动探测数据报文检验是否还保持连接
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    // 和Nagle算法有关
                    // Nagle算法是将小的数据包组装为更大的帧然后进行发送，而不是输入一次发送一次,
                    // 因此在数据包不足的时候会等待其他数据的到了，组装成大的数据包进行发送，虽然该方式有效提高网络的有效负载，但是却造成了延时.
                    // 该参数就是禁止使用Nagle算法，使用小数据即时传输
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    // socketChannel上的handler
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new CommonDecoder())   // 添加解码器
                                    .addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS))
                                    // .addLast(new CommonEncoder(new JsonSerializer()))   // 添加JSON序列化器的编码器
                                    .addLast(new CommonEncoder(serializer))
                                    .addLast(new NettyServerHandler()); // 添加数据处理器
                        }
                    });
            // 绑定监听端口
            ChannelFuture futrue = serverBootstrap.bind(port).sync();
            futrue.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("启动服务器时有错误发生: ", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
