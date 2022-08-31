package example.rpc.transport;

import example.rpc.entity.RpcRequest;
import example.rpc.serializer.CommonSerializer;

/**
 * 客户端类通用接口
 * @Author admin
 * @Date 2022/8/8 20:51
 * @Version 1.0
 */
public interface RpcClient {

    int DEFAULT_SERIALIZER = CommonSerializer.KRYO_SERIALIZER;

    Object sendRequest(RpcRequest rpcRequest);

}
