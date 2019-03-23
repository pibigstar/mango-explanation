package mango.codec;

import mango.common.URL;
import mango.common.URLParam;
import mango.core.DefaultRequest;
import mango.core.DefaultResponse;
import mango.core.extension.ExtensionLoader;
import mango.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * ${DESCRIPTION}
 *
 * @author Ricky Fung
 */
public class DefaultCodec extends AbstractCodec {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public byte[] encode(URL url, Object message) throws IOException {
        // 获取URL参数中的序列化值，默认是 protostuff
        String serialization = url.getParameter(URLParam.serialization.getName(), URLParam.serialization.getValue());
        logger.info("Codec encode serialization:{}", serialization);
        // 根据序列化值去获取获取SPI扩展实例对象
        return serialize(message, ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(serialization));
    }

    @Override
    public Object decode(URL url, byte messageType, byte[] data) throws IOException {
        String serialization = url.getParameter(URLParam.serialization.getName(), URLParam.serialization.getValue());
        logger.info("Codec decode serialization:{}", serialization);
        if(messageType == Constants.FLAG_REQUEST) {
            return deserialize(data, DefaultRequest.class, ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(serialization));
        }
        return deserialize(data, DefaultResponse.class, ExtensionLoader.getExtensionLoader(Serializer.class).getExtension(serialization));
    }
}
