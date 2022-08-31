package example.rpc.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 测试用api的实体
 * @Author admin
 * @Date 2022/8/6 12:31
 * @Version 1.0
 * 该对象需要实现Serializable接口，因为需要在调用过程中从客户端传给服务端
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HelloObject implements Serializable {
    private Integer id;
    private String message;
}
