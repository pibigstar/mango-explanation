package mango.core;

/**
 * 抽象的失败响应实体
 * @author Ricky Fung
 */
public abstract class AbstractResponseFuture<T> implements ResponseFuture<T> {
    //状态
    protected volatile FutureState state = FutureState.NEW;
    //处理开始时间
    protected final long createTime = System.currentTimeMillis();
    // 超时时间
    protected long timeoutInMillis;

    public AbstractResponseFuture(long timeoutInMillis) {
        this.timeoutInMillis = timeoutInMillis;
    }

    @Override
    public boolean isCancelled() {
        return this.state == FutureState.CANCELLED;
    }

    @Override
    public boolean isDone() {
        return this.state == FutureState.DONE;
    }

    @Override
    public boolean isTimeout() {
        // 如果创建时的时间 + 超时时间 大于当前时间就说明超时了
        return createTime + timeoutInMillis > System.currentTimeMillis();
    }
}
