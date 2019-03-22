package mango.rpc;

import mango.common.URL;

/**
 * reference to a service
 * 服务引用  Reference ——> Caller ——> Node
 * @author Ricky Fung
 */
public interface Reference<T> extends Caller<T> {

    /**
     * 当前Reference的调用次数
     * @return
     */
    int activeCount();

    URL getServiceUrl();
}
