package example.rpc.exception;

/**
 * 序列化异常
 * @Author admin
 * @Date 2022/8/9 21:59
 * @Version 1.0
 */
public class SerializerException extends RuntimeException{
    public SerializerException(String msg) {
        super(msg);
    }
}
