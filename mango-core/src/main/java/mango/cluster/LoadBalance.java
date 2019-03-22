package mango.cluster;

import mango.core.Request;
import mango.core.extension.SPI;
import mango.core.extension.Scope;
import mango.rpc.Reference;
import java.util.List;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
@SPI(scope = Scope.PROTOTYPE)
public interface LoadBalance<T> {

    void setReferences(List<Reference<T>> references);

    /**
     * 从服务集合中随机抽取一个服务
     */
    Reference<T> select(Request request);
}
