package mango.filter;

import mango.core.Request;
import mango.core.Response;
import mango.core.extension.SPI;
import mango.rpc.Caller;

/**
 * 过滤器接口
 * @author Ricky Fung
 */
@SPI
public interface Filter {

    Response filter(Caller<?> caller, Request request);

}
