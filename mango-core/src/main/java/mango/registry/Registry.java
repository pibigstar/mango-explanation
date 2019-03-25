package mango.registry;

import mango.common.URL;

/**
 * 注册中心接口
 *
 * @author Ricky Fung
 */
public interface Registry extends RegistryService, DiscoveryService {

    URL getUrl();

    void close();
}
