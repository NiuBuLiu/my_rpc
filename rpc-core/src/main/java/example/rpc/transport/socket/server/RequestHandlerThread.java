package example.rpc.transport.socket.server;

import example.rpc.handler.RequestHandler;
import example.rpc.entity.RpcRequest;
import example.rpc.entity.RpcResponse;
import example.rpc.provider.ServiceProvider;
import example.rpc.serializer.CommonSerializer;
import example.rpc.transport.socket.util.ObjectReader;
import example.rpc.transport.socket.util.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;

/**
 * 实际进行过程中调用的工作线程
 * @Author admin
 * @Date 2022/8/6 21:21
 * @Version 1.0
 */
public class RequestHandlerThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandlerThread.class);

    private Socket socket;
    private RequestHandler requestHandler;
    private CommonSerializer serializer;


    public RequestHandlerThread(Socket socket, RequestHandler requestHandler, CommonSerializer serializer) {
        this.socket = socket;
        this.requestHandler = requestHandler;
        this.serializer = serializer;
    }

    /**
     * 接收到rpc请求对象，获取其接口名，再通过注册表得到服务对象，
     * 再在rpc请求对象处理类中，通过反射调用服务的方法
     */
    @Override
    public void run() {
        try (InputStream inputStream = socket.getInputStream();
             OutputStream outputStream = socket.getOutputStream()) {
            RpcRequest rpcRequest = (RpcRequest) ObjectReader.readObject(inputStream);
            Object result = requestHandler.handle(rpcRequest);
            RpcResponse<Object> response = RpcResponse.success(result, rpcRequest.getRequestId());
            ObjectWriter.writeObject(outputStream, response, serializer);
        } catch (IOException e) {
            logger.error("调用或者发送时有错误产生", e);
        }
    }
}
