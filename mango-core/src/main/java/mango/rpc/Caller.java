package mango.rpc;

import mango.core.Request;
import mango.core.Response;

/**
 * 调用者接口
 * @author Ricky Fung
 */
public interface Caller<T> extends Node {

    Class<T> getInterface();

    Response call(Request request);

}
