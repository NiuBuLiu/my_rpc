package example.rpc.serializer;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import example.rpc.entity.RpcRequest;
import example.rpc.entity.RpcResponse;
import example.rpc.enumeration.SerializerCode;
import example.rpc.exception.SerializerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @Author admin
 * @Date 2022/8/9 21:31
 * @Version 1.0
 */
public class KryoSerializer implements CommonSerializer{

    private static final Logger logger = LoggerFactory.getLogger(KryoSerializer.class);

    // Kryo可能会存在线程安全问题，文档推荐放在ThreaLocal中，一个线程一个Kryo。
    private static final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(() -> {
        Kryo kryo = new Kryo();
        kryo.register(RpcResponse.class);
        kryo.register(RpcRequest.class);
        // 循环引用检测打开，可以防止栈溢出
        kryo.setReferences(true);
        // 强制要求注册类（注册行为无法保证多个 JVM 内同一个类的注册编号相同；而且业务系统中大量的 Class 也难以一一注册）
        kryo.setRegistrationRequired(false);
        return kryo;
    });

    // 序列化，先创建一个Output对象（Kryo 框架的概念），接着使用writeObject方法将对象写入Output中，最后调用Output对象的toByte()方法即可获得对象的字节数组。
    @Override
    public byte[] serializer(Object object) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             Output output = new Output(byteArrayOutputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            kryo.writeObject(output, object);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            logger.error("序列化时有错误发生:", e);
            throw new SerializerException("序列化时有错误发生");
        }
    }

    // 反序列化则是从Input对象中直接readObject，这里只需要传入对象的类型，而不需要具体传入每一个属性的类型信息
    @Override
    public Object deserialize(byte[] bytes, Class<?> clazz) {
        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
             Input input = new Input(byteArrayInputStream)) {
            Kryo kryo = kryoThreadLocal.get();
            Object o = kryo.readObject(input, clazz);
            kryoThreadLocal.remove();
            return o;
        } catch (Exception e) {
            logger.error("反序列化时有错误发生:", e);
            throw new SerializerException("反序列化时有错误发生");
        }
    }

    @Override
    public int getCode() {
        // return SerializerCode.KRYO.getCode();
         return SerializerCode.valueOf("KRYO").getCode();
    }
}
