package mango.registry;

import mango.common.URL;
import mango.core.extension.SPI;
import mango.core.extension.Scope;

/**
 * 注册中心工厂接口
 *
 * @author Ricky Fung
 */
@SPI(scope = Scope.SINGLETON)
public interface RegistryFactory {

    Registry getRegistry(URL url);
}
