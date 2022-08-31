package example.rpc.transport.socket.server;

import example.rpc.enumeration.RpcError;
import example.rpc.exception.RpcException;
import example.rpc.handler.RequestHandler;
import example.rpc.hook.ShutdownHook;
import example.rpc.provider.ServiceProviderImpl;
import example.rpc.registry.ServiceRegistry;
import example.rpc.registry.ZookeeperServiceRegistry;
import example.rpc.serializer.CommonSerializer;
import example.rpc.transport.AbstractRpcServer;
import example.rpc.transport.RpcServer;
import example.rpc.provider.ServiceProvider;
import example.rpc.factory.ThreadPoolFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * Socket方式远程方法调用的提供者（服务端）
 * @Author admin
 * @Date 2022/8/6 21:07
 * @Version 1.0
 */
public class SocketServer extends AbstractRpcServer {

    private static final Logger logger = LoggerFactory.getLogger(SocketServer.class);

    private final ExecutorService threadPool;;
    private final String host;
    private final int port;
    private CommonSerializer serializer;
    private RequestHandler requestHandler = new RequestHandler();

    private final ServiceRegistry serviceRegistry;
    private final ServiceProvider serviceProvider;

    public SocketServer(String host, int port) {
        this(host, port, DEFAULT_SERIALIZER);
    }

    public SocketServer(String host, int port, Integer serializer) {
        this.host = host;
        this.port = port;
        threadPool = ThreadPoolFactory.createDefaultThreadPool("socket-rpc-server");
        this.serviceRegistry = new ZookeeperServiceRegistry();
        this.serviceProvider = new ServiceProviderImpl();
        this.serializer = CommonSerializer.getByCode(serializer);
        scanService();
    }

    /*@Override
    public <T> void publishService(T service, Class<T> serviceClass) {
        if(serializer == null) {
            logger.error("未设置序列化器");
            throw new RpcException(RpcError.SERIALIZER_NOT_FOUND);
        }
        serviceProvider.addServiceProvider(service, serviceClass);
        serviceRegistry.register(serviceClass.getCanonicalName(), new InetSocketAddress(host, port));
        start();
    }*/

    @Override
    public void start() {

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("服务器正在启动...");
            ShutdownHook.getShutdownHook().addClearAllHook();
            Socket socket;
            while((socket = serverSocket.accept()) != null) {
                // logger.info("客户端连接！IP为：" + socket.getInetAddress());
                logger.info("消费者连接: {}:{}", socket.getInetAddress(), socket.getPort());
                threadPool.execute(new RequestHandlerThread(socket, requestHandler, serializer));
            }
            threadPool.shutdown();
        } catch (IOException e) {
            logger.error("服务器启动时有错误发生", e);
        }
    }

}
