package example.rpc.transport.netty.client;

import example.rpc.entity.RpcResponse;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author admin
 * @Date 2022/8/12 11:25
 * @Version 1.0
 */
public class UnprocessedRequests {

    private static ConcurrentHashMap<String, CompletableFuture<RpcResponse>> unprocessedResponseFutures = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<RpcResponse> futrue) {
        unprocessedResponseFutures.put(requestId, futrue);
    }

    public void remove(String requestId) {
        unprocessedResponseFutures.remove(requestId);
    }

    public void complete(RpcResponse rpcResponse) {
        CompletableFuture<RpcResponse> future = unprocessedResponseFutures.remove(rpcResponse.getRequestId());
        if (future != null) {
            future.complete(rpcResponse);
        } else {
            throw new IllegalStateException();
        }
    }

}
