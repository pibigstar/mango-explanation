package mango.transport;

import mango.core.Request;
import mango.core.Response;
import mango.core.ResponseFuture;
import mango.exception.TransportException;

/**
 * Netty客户端
 *
 * @author Ricky Fung
 */
public interface NettyClient extends Endpoint {

    /**
     * 反射调用方法（同步）
     */
    Response invokeSync(final Request request) throws InterruptedException, TransportException;
    /**
     * 反射调用方法（异步）
     */
    ResponseFuture invokeAsync(final Request request) throws InterruptedException, TransportException;
    /**
     * 反射调用方法（单线程）
     */
    void invokeOneway(final Request request) throws InterruptedException, TransportException;

}
