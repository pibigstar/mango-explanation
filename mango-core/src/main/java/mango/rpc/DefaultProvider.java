package mango.rpc;

import mango.common.URL;
import mango.core.DefaultResponse;
import mango.core.Request;
import mango.core.Response;
import mango.exception.RpcBizException;
import mango.exception.RpcFrameworkException;

import java.lang.reflect.Method;

/**
 * 默认服务提供者
 * @author Ricky Fung
 */
public class DefaultProvider<T> extends AbstractProvider<T> {
    // 方法执行对象
    protected T proxyImpl;

    public DefaultProvider(T proxyImpl, URL url, Class<T> clz) {
        super(url, clz);
        this.proxyImpl = proxyImpl;
    }

    @Override
    public Class<T> getInterface() {
        return clz;
    }

    /**
     * 反射执行request中的方法
     * 1. 先根据request中的方法参数类型获取到具体方法对象
     * 2. 反射执行该方法对象
     */
    @Override
    public Response invoke(Request request) {

        DefaultResponse response = new DefaultResponse();
        response.setRequestId(request.getRequestId());

        Method method = lookup(request);
        if (method == null) {
            RpcFrameworkException exception = new RpcFrameworkException("Service method not exist: " + request.getInterfaceName() + "." + request.getMethodName());
            response.setException(exception);
            return response;
        }
        try {
            method.setAccessible(true);
            Object result = method.invoke(proxyImpl, request.getArguments());
            response.setResult(result);
        } catch (Exception e) {
            response.setException(new RpcBizException("invoke failure", e));
        }
        return response;
    }
}
