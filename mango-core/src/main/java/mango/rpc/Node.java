package mango.rpc;

import mango.common.URL;

/**
 * 节点接口（一个节点应该具备的功能）
 *
 * @author Ricky Fung
 */
public interface Node {

    void init();

    void destroy();
    // 是否可用
    boolean isAvailable();
    // 描述
    String desc();

    URL getUrl();
}
