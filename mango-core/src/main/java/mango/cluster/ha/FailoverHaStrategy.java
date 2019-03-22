package mango.cluster.ha;

import mango.cluster.HaStrategy;
import mango.cluster.LoadBalance;
import mango.common.URL;
import mango.common.URLParam;
import mango.core.Request;
import mango.core.Response;
import mango.exception.RpcFrameworkException;
import mango.rpc.Reference;
import mango.util.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

/**
 * ${DESCRIPTION}
 * 故障转移策略
 * @author Ricky Fung
 */
public class FailoverHaStrategy<T> implements HaStrategy<T> {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public Response call(Request request, LoadBalance loadBalance) {
        // 随机抽取一个服务
        Reference<T> reference = loadBalance.select(request);
        // 获取服务的URL
        URL refUrl = reference.getUrl();
        // 根据枚举中的name，从URL对象中获取其值，如果为空，则默认值为枚举中的value
        int tryCount = refUrl.getIntParameter(URLParam.retries.getName(), URLParam.retries.getIntValue());
        if(tryCount<0){
            tryCount = 0;
        }
        for (int i = 0; i <= tryCount; i++) {
            // 尝试抽取服务调用回调函数
            reference = loadBalance.select(request);
            try {
                // 调用成功之后就return了
                return reference.call(request);
            } catch (RuntimeException e) {
                // 对于业务异常，直接抛出,抛出后就不会继续运行了
                if (ExceptionUtil.isBizException(e)) {
                    throw e;
                    // 如果大于尝试次数直接抛出
                } else if (i >= tryCount) {
                    throw e;
                }
                logger.warn(String.format("FailoverHaStrategy Call false for request:%s error=%s", request, e.getMessage()));
            }
        }
        throw new RpcFrameworkException("FailoverHaStrategy.call should not come here!");
    }
}
