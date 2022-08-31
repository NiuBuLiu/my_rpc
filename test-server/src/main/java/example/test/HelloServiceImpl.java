package example.test;

import example.rpc.api.HelloObject;
import example.rpc.api.HelloService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author admin
 * @Date 2022/8/6 12:40
 * @Version 1.0
 */
public class HelloServiceImpl implements HelloService {
    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);

    @Override
    public String hello(HelloObject object) {
        logger.info("接收到: {}", object.getMessage());
        return "这是调用的返回值，id=" + object.getId();
    }
}
