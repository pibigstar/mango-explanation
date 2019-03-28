package mango.rpc;

import mango.core.DefaultResponse;
import mango.core.Request;
import mango.core.Response;
import mango.exception.RpcBizException;
import mango.exception.RpcFrameworkException;
import mango.util.FrameworkUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 消息路由，获取执行者，请求request
 * @author Ricky Fung
 */
public class MessageRouter implements MessageHandler {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private ConcurrentHashMap<String, Provider<?>> providers = new ConcurrentHashMap<>();

    public MessageRouter() {}

    /**
     * 初始化时将provider加入到列表中
     */
    public MessageRouter(Provider<?> provider) {
        addProvider(provider);
    }

    @Override
    public Response handle(Request request) {

        String serviceKey = FrameworkUtils.getServiceKey(request);
        // 根据serviceKey获取调用者
        Provider<?> provider = providers.get(serviceKey);

        if (provider == null) {
            logger.error(this.getClass().getSimpleName() + " handler Error: provider not exist serviceKey=" + serviceKey);
            RpcFrameworkException exception = new RpcFrameworkException(this.getClass().getSimpleName() + " handler Error: provider not exist serviceKey=" + serviceKey );
            DefaultResponse response = new DefaultResponse();
            response.setException(exception);
            return response;
        }
        return call(request, provider);
    }

    protected Response call(Request request, Provider<?> provider) {
        try {
            // 通过调用者执行call方法
            return provider.call(request);
        } catch (Exception e) {
            DefaultResponse response = new DefaultResponse();
            response.setException(new RpcBizException("provider call process error", e));
            return response;
        }
    }

    /**
     * 添加一个执行者
     */
    public synchronized void addProvider(Provider<?> provider) {
        String serviceKey = FrameworkUtils.getServiceKey(provider.getUrl());
        if (providers.containsKey(serviceKey)) {
            throw new RpcFrameworkException("provider alread exist: " + serviceKey);
        }
        providers.put(serviceKey, provider);
        logger.info("RequestRouter addProvider: url=" + provider.getUrl());
    }
    /**
     * 移除一个执行者
     */
    public synchronized void removeProvider(Provider<?> provider) {
        String serviceKey = FrameworkUtils.getServiceKey(provider.getUrl());
        providers.remove(serviceKey);
        logger.info("RequestRouter removeProvider: url=" + provider.getUrl());
    }
}
