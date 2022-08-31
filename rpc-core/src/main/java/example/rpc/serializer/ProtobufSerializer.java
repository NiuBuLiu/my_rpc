package example.rpc.serializer;

import example.rpc.enumeration.SerializerCode;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 使用ProtoBuf的序列化器
 * @Author admin
 * @Date 2022/8/11 2:09
 * @Version 1.0
 */
public class ProtobufSerializer implements CommonSerializer{

    private static final Logger logger = LoggerFactory.getLogger(ProtobufSerializer.class);

    private LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
    private Map<Class<?>, Schema<?>> schemaCache = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public byte[] serializer(Object object) {
        Class<?> clazz = object.getClass();
        Schema schema = getSchema(clazz);
        byte[] data;
        try {
            data = ProtostuffIOUtil.toByteArray(object, schema, buffer);
        } finally {
            buffer.clear();
        }
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        Schema schema = getSchema(clazz);
        Object object = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(bytes, object, schema);
        return object;
    }

    @Override
    public int getCode() {
        return SerializerCode.PROTOBUF.getCode();
    }

    @SuppressWarnings("unchecked")
    private Schema getSchema(Class clazz) {
        Schema<?> schema = schemaCache.get(clazz);
        if(Objects.isNull(schema)) {
            // 这个schema通过RuntimeSchema进行懒创建并缓存
            // 所以可以一直调用RuntimeSchema.getSchema(),这个方法是线程安全的
            schema = RuntimeSchema.getSchema(clazz);
            if (Objects.nonNull(schema)) {
                schemaCache.put(clazz, schema);
            }
        }
        return schema;
    }

}
