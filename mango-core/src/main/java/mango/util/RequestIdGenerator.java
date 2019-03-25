package mango.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 生成requestId工具类
 *
 * @author Ricky Fung
 */
public class RequestIdGenerator {
    // 使用AtomicLong防止多线程id重复
    private static final AtomicLong idGenerator = new AtomicLong(1);

    public static long getRequestId() {
        return idGenerator.getAndIncrement();
    }
}
