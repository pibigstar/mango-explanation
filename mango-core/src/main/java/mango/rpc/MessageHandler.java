package mango.rpc;

import mango.core.Request;
import mango.core.Response;
import mango.core.extension.SPI;
import mango.util.Constants;

/**
 *  消息处理器
 * @author Ricky Fung
 */
public interface MessageHandler {
    // 处理消息
    Response handle(Request request);

}
