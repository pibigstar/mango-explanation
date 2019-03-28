package mango.rpc;

import mango.core.Request;
import mango.core.Response;

/**
 * 调用者（执行者）接口
 * @author Ricky Fung
 */
public interface Caller<T> extends Node {

    Class<T> getInterface();
    // 执行request
    Response call(Request request);

}
