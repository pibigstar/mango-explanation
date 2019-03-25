package mango.registry;

import mango.common.URL;

import java.util.List;

/**
 * 监听唤醒接口
 * @author Ricky Fung
 */
public interface NotifyListener {

    void notify(URL registryUrl, List<URL> urls);
}
