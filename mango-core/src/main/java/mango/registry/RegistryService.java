package mango.registry;

import mango.common.URL;

/**
 * 注册中心服务层接口
 * @author Ricky Fung
 */
public interface RegistryService {

    /**
     * register service to registry
     *
     * @param url
     */
    void register(URL url) throws Exception;

    /**
     * unregister service to registry
     *
     * @param url
     */
    void unregister(URL url) throws Exception;

}
