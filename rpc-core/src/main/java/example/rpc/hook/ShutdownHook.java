package example.rpc.hook;

import example.rpc.factory.ThreadPoolFactory;
import example.rpc.registry.util.CuratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 由于启动完服务端后把服务端关闭了，不会自动注销Zookeeper中对应的服务信息
 * 就会导致客户端再次向Zookeeper请求服务时，会获取到已经关闭的服务，最终有可能因为连接不到服务器而调用失败
 * 所以需要在服务端关闭之前自动向Zookeeper注销服务，但由于不知道什么时候关闭服务端，故需要钩子
 * 钩子：
 *  在某些事件发生后自动去调用的方法，将注销服务的方法写到关闭系统的钩子方法中即可
 * @Author admin
 * @Date 2022/8/11 21:56
 * @Version 1.0
 */
public class ShutdownHook {

    private static final Logger logger = LoggerFactory.getLogger(ShutdownHook.class);

    private static final ShutdownHook shutdownHook = new ShutdownHook();

    public static ShutdownHook getShutdownHook() {
        return shutdownHook;
    }

    public void addClearAllHook() {
        logger.info("关闭后将自动注销所有服务");
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            CuratorUtils.clearRegistry();
            ThreadPoolFactory.shutDownAll();
        }));
    }

}
