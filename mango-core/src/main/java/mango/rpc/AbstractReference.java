package mango.rpc;

import mango.common.URL;
import mango.core.Request;
import mango.core.Response;
import mango.exception.RpcFrameworkException;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *  抽象服务引用
 * @author Ricky Fung
 */
public abstract class AbstractReference<T> implements Reference<T> {
    protected Class<T> clz;
    private URL url;
    protected URL serviceUrl;

    protected AtomicInteger activeCounter = new AtomicInteger(0);

    public AbstractReference(Class<T> clz, URL serviceUrl) {
        this.clz = clz;
        this.serviceUrl = serviceUrl;
    }

    public AbstractReference(Class<T> clz, URL url, URL serviceUrl) {
        this.clz = clz;
        this.url = url;
        this.serviceUrl = serviceUrl;
    }

    @Override
    public URL getServiceUrl() {
        return serviceUrl;
    }

    @Override
    public Class<T> getInterface() {
        return clz;
    }

    @Override
    public Response call(Request request) {
        if (!isAvailable()) {
            throw new RpcFrameworkException(this.getClass().getName() + " call Error: node is not available, url=" + url.getUri());
        }
        // 计数加1
        incrActiveCount(request);
        Response response = null;
        try {
            // doCall，延迟到子类具体实现
            response = doCall(request);
            return response;
        } finally {
            // 计数减1
            decrActiveCount(request, response);
        }

    }

    @Override
    public int activeCount() {
        return activeCounter.get();
    }
    // 定一个抽象方法，具体实现放到子类中
    protected abstract Response doCall(Request request);

    protected void decrActiveCount(Request request, Response response) {
        activeCounter.decrementAndGet();
    }

    protected void incrActiveCount(Request request) {
        activeCounter.incrementAndGet();
    }

    @Override
    public String desc() {
        return "[" + this.getClass().getName() + "] url=" + url;
    }

    @Override
    public URL getUrl() {
        return url;
    }
}
