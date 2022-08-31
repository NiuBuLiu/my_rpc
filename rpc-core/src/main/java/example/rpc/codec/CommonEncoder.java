package example.rpc.codec;

import example.rpc.entity.RpcRequest;
import example.rpc.enumeration.PackageType;
import example.rpc.serializer.CommonSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 通用编码拦截器
 * 继承MessageToByteEncoder类，就是把实际要发送的Message数组转化成Byte数组
 * 并根据协议格式，将各个字段写到管道中
 * @Author admin
 * @Date 2022/8/8 21:38
 * @Version 1.0
 */
public class CommonEncoder extends MessageToByteEncoder {

    // 魔数，4字节魔数，用于标识一个协议包
    private static final int MAGIC_NUMBER = 0xCAFEBABE;

    private final CommonSerializer serializer;

    public CommonEncoder(CommonSerializer serializer) {
        this.serializer = serializer;
    }

    /**
     * 编码为自定义协议，该自定义协议包括5个部分
     * Magic Number(4字节): 魔数，用于表示这是一个协议包
     * Package Type(4字节): 包类型，用于表示这是一个请求包还是一个响应包
     * Serializer Type(4字节): 用于标识序列化的类型，是使用了哪一个序列化器
     * Data Length(4字节): 用于表示实际数据长度，主要用于防止粘包
     * Data Bytes: 序列化的实际数据
     * @param ctx
     * @param msg
     * @param out
     * @throws Exception
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        out.writeInt(MAGIC_NUMBER);
        if (msg instanceof RpcRequest) {
            out.writeInt(PackageType.REQUEST_PACK.getCode());
        } else {
            out.writeInt(PackageType.RESPONSE_PACK.getCode());
        }
        out.writeInt(serializer.getCode());
        byte[] bytes = serializer.serializer(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
