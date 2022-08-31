package example.rpc.serializer;

/**
 * 通用的序列化反序列化接口
 * @Author admin
 * @Date 2022/8/8 21:42
 * @Version 1.0
 */
public interface CommonSerializer {

    Integer KRYO_SERIALIZER = 0;
    Integer JSON_SERIALIZER = 1;
    Integer HESSIAN_SERIALIZER = 2;
    Integer PROTOBUF_SERIALIZER = 3;

    Integer DEFAULT_SERIALIZER = KRYO_SERIALIZER;

    /**
     * 序列化
     * @param object
     * @return
     */
    byte[] serializer(Object object);

    /**
     * 反序列化
     * @param bytes
     * @return
     */
    Object deserialize(byte[] bytes, Class<?> clazz);

    /**
     * 获得该序列化器的编号
     * @return
     */
    int getCode();

    /**
     * 根据已有编号获取序列化器
     * @param code
     * @return
     */
    static CommonSerializer getByCode(int code) {
        switch (code) {
            case 0:
                return new KryoSerializer();
            case 1:
                return new JsonSerializer();
            case 2:
                return new HessianSerializer();
            case 3:
                return new ProtobufSerializer();
            default:
                return null;
        }
    }

}
