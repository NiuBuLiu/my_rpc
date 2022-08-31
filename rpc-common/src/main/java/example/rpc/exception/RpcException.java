package example.rpc.exception;

import example.rpc.enumeration.RpcError;

/**
 * RPC调用异常
 * @Author admin
 * @Date 2022/8/7 21:01
 * @Version 1.0
 */
public class RpcException extends RuntimeException{
    public RpcException(RpcError error, String detail) {
        super(error.getMessage() + ": " + detail);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(RpcError error) {
        super(error.getMessage());
    }
}
