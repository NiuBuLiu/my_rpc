package example.rpc.codec;

import example.rpc.entity.RpcRequest;
import example.rpc.entity.RpcResponse;
import example.rpc.enumeration.PackageType;
import example.rpc.enumeration.RpcError;
import example.rpc.exception.RpcException;
import example.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ReplayingDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 通用的解码拦截器
 * 继承ReplayingDecoder，用于将收到的字节序列还原为实际对象
 * 通过对字段的校验，取出序列化器的编号，找到正确的反序列化方式
 * 并且读入length字段，确定数据包的长度，防止粘包
 * 最后读入正确大小的字节数组，反序列化为正确的对象
 * @Author admin
 * @Date 2022/8/8 21:38
 * @Version 1.0
 */
public class CommonDecoder extends ReplayingDecoder {

    private static final Logger logger = LoggerFactory.getLogger(CommonDecoder.class);
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magic = in.readInt();
        if (magic != MAGIC_NUMBER) {
            logger.error("不识别的协议包: {}", magic);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        int packageCode = in.readInt();
        Class<?> packageClass;
        if (packageCode == PackageType.REQUEST_PACK.getCode()) {
            packageClass = RpcRequest.class;
        } else if (packageCode == PackageType.RESPONSE_PACK.getCode()) {
            packageClass = RpcResponse.class;
        } else {
            logger.error("不识别的协议包: {}", packageCode);
            throw new RpcException(RpcError.UNKNOWN_PROTOCOL);
        }
        int serializerCode = in.readInt();
        CommonSerializer serializer = CommonSerializer.getByCode(serializerCode);
        if(serializer == null) {
            logger.error("不识别的反序列化器: {}", serializerCode);
            throw new RpcException(RpcError.UNKNOWN_SERIALIZER);
        }
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes);
        Object obj = serializer.deserialize(bytes, packageClass);
        out.add(obj);
    }
}
