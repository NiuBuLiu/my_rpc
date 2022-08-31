package example.rpc.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 消费者向提供者发送的请求对象
 * @Author admin
 * @Date 2022/8/6 12:59
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcRequest implements Serializable {

    /**
     * 请求号
     */
    private String requestId;
    /**
     * 待调用接口名称
     */
    private String interfaceName;
    /**
     * 待调用方法名称
     */
    private String methodName;
    /**
     * 待调用方法的参数
     */
    private Object[] parameters;
    /**
     * 调用方法参数的类型
     */
    private Class<?>[] paramsTypes;
    /**
     * 是否是心跳包
     */
    private Boolean heartBeat;

}
