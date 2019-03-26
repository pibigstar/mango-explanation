package mango.rpc;

/**
 * 服务暴露接口
 *
 * @author Ricky Fung
 */
public interface Exporter<T> extends Node {

    Provider<T> getProvider();

    void unexport();
}
