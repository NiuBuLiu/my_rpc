package example.rpc.serializer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import example.rpc.entity.RpcRequest;
import example.rpc.enumeration.SerializerCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * 使用JSON格式的序列化器
 * @Author admin
 * @Date 2022/8/8 21:46
 * @Version 1.0
 */
public class JsonSerializer implements CommonSerializer{

    private static final Logger logger = LoggerFactory.getLogger(JsonSerializer.class);

    // 利用该对象来解析json，进行序列化和反序列化
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public byte[] serializer(Object object) {
        try {
            return objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e) {
            logger.error("序列化时有错误发生: {}", e.getMessage());
            e.printStackTrace();
        }
        return new byte[0];
    }

    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try {
            Object obj = objectMapper.readValue(bytes, clazz);
            if (obj instanceof RpcRequest) {
                obj = handlerRequest(obj);
            }
            return obj;
        } catch (IOException e) {
            logger.error("反序列化时有错误发生: {}", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 这里由于使用JSON序列化和反序列化Object数组，因为Object是一个十分模糊的类型，无法保证反序列化后仍然为原实例类型，故需要重新判断处理
     * 也就是利用RpcRequest的另一个字段ParamTypes来获取Object数组中每个实例的实际类，辅助反序列化
     * @param object
     * @return
     * @throws IOException
     */
    private Object handlerRequest(Object object) throws IOException {
        RpcRequest rpcRequest = (RpcRequest) object;
        for (int i = 0; i < rpcRequest.getParamsTypes().length; i++) {
            Class<?> clazz = rpcRequest.getParamsTypes()[i];
            // 这儿isAssignableFrom是用于判断当前clazz对象所表示的类是否是参数中传递的Class对象所表示的类
            if (!clazz.isAssignableFrom(rpcRequest.getParameters()[i].getClass())) {
                byte[] bytes = objectMapper.writeValueAsBytes(rpcRequest.getParameters()[i]);
                rpcRequest.getParameters()[i] = objectMapper.readValue(bytes, clazz);
            }
        }
        return rpcRequest;
    }

    @Override
    public int getCode() {
        return SerializerCode.valueOf("JSON").getCode();
    }
}
