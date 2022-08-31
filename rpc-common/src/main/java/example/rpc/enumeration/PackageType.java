package example.rpc.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 包类型
 * 0 表示为请求包
 * 1 表示为响应包
 * @Author admin
 * @Date 2022/8/8 22:22
 * @Version 1.0
 */
@AllArgsConstructor
@Getter
public enum PackageType {
    REQUEST_PACK(0),
    RESPONSE_PACK(1);

    private final int code;
}
