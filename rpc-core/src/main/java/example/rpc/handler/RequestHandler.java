package example.rpc.handler;

import example.rpc.entity.RpcRequest;
import example.rpc.entity.RpcResponse;
import example.rpc.enumeration.ResponseCode;
import example.rpc.provider.ServiceProvider;
import example.rpc.provider.ServiceProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 进程过程调用的处理器
 * @Author admin
 * @Date 2022/8/7 21:26
 * @Version 1.0
 */
public class RequestHandler {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final ServiceProvider serviceProvider;

    static {
        serviceProvider = new ServiceProviderImpl();
    }

    public Object handle(RpcRequest rpcRequest) {
        Object service = serviceProvider.getServiceProvider(rpcRequest.getInterfaceName());
        return invokeTargetMethod(rpcRequest, service);
        /*Object result = null;
        try {
            result = invokeTargetMethod(rpcRequest, service);
            logger.info("服务:{} 成功调用方法:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (InvocationTargetException | IllegalAccessException e) {
            logger.error("调用或发送时有错误发生：", e);
        }
        return result;*/
    }

    /**
     * 通过反射调用目标方法
     * @param rpcRequest
     * @param service
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    public Object invokeTargetMethod(RpcRequest rpcRequest, Object service){
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamsTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            logger.info("服务:{} 成功调用方法:{}", rpcRequest.getInterfaceName(), rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            return RpcResponse.fail(ResponseCode.NOT_FOUND_METHOD, rpcRequest.getRequestId());
        }
        return result;
        /*Method method;
        try {
            method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamsTypes());
        } catch (NoSuchMethodException e) {
            return RpcResponse.fail(ResponseCode.NOT_FOUND_CLASS, rpcRequest.getRequestId());
        }
        return method.invoke(service, rpcRequest.getParameters());*/
    }

}
