package mango.codec;

import java.io.IOException;

/**
 * ${DESCRIPTION}
 * 定义接口——> 抽象类实现此接口 ——> 默认类去继承此抽象类
 * @author Ricky Fung
 */
public abstract class AbstractCodec implements Codec {

    /**
     * 定义为protected 让子类去具体实现
     */
    protected byte[] serialize(Object message, Serializer serializer) throws IOException {
        if (message == null) {
            return null;
        }
        return serializer.serialize(message);
    }

    protected Object deserialize(byte[] data, Class<?> type, Serializer serializer) throws IOException {
        if (data == null) {
            return null;
        }
        return serializer.deserialize(data, type);
    }
}
