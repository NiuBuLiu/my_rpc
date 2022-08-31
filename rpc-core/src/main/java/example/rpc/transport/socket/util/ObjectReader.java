package example.rpc.transport.socket.util;

import example.rpc.entity.RpcRequest;
import example.rpc.entity.RpcResponse;
import example.rpc.enumeration.PackageType;
import example.rpc.enumeration.RpcError;
import example.rpc.exception.RpcException;
import example.rpc.serializer.CommonSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * @Author admin
 * @Date 2022/8/12 21:20
 * @Version 1.0
 */
public class ObjectReader {

    private static final Logger logger = LoggerFactory.getLogger(ObjectReader.class);
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    public static Object readObject(InputStream in) throws IOException {
        byte[] bytes = new byte[4];
        in.read(bytes);
        int magic = bytesToInt(bytes);
        if(magic != MAGIC_NUMBER) {
            logger.error("不识别的协议包: {}", magic);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        in.read(bytes);
        int packageCode = bytesToInt(bytes);
        Class<?> packageClass;
        if (packageCode == PackageType.REQUEST_PACK.getCode()) {
            packageClass = RpcRequest.class;
        } else if (packageCode == PackageType.RESPONSE_PACK.getCode()){
            packageClass = RpcResponse.class;
        } else {
            logger.error("不识别的数据包: {}", packageCode);
            throw new RpcException(RpcError.UNKNOWN_PACKAGE_TYPE);
        }
        in.read(bytes);
        int serializerCode = bytesToInt(bytes);
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if (serializer == null) {
            logger.error("不识别的反序列化器: {}", serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        in.read(bytes);
        int length = bytesToInt(bytes);
        byte[] data = new byte[length];
        in.read(data);
        return serializer.deserialize(data, packageClass);
    }

    public static int bytesToInt(byte[] src) {
        int value;
        value = ((src[0] & 0xFF)<<24)
                |((src[1] & 0xFF)<<16)
                |((src[2] & 0xFF)<<8)
                |(src[3] & 0xFF);
        return value;
    }

}
