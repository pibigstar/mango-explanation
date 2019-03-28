package mango.transport;

import mango.codec.Codec;
import mango.common.URL;
import mango.common.URLParam;
import mango.core.extension.ExtensionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * 抽象客户端
 * @author Ricky Fung
 */
public abstract class AbstractClient implements NettyClient {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    /**
     * 本地Socket地址
     */
    protected InetSocketAddress localAddress;
    /**
     * 远程Socket地址
     */
    protected InetSocketAddress remoteAddress;

    protected URL url;
    protected Codec codec;

    protected volatile ChannelState state = ChannelState.NEW;

    public AbstractClient(URL url) {
        this.url = url;
        this.codec = ExtensionLoader.getExtensionLoader(Codec.class).getExtension(url.getParameter(URLParam.codec.getName(), URLParam.codec.getValue()));
        logger.info("NettyClient init url:" + url.getHost() + "-" + url.getPath() + ", use codec:" + codec.getClass().getSimpleName());
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return localAddress;
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return remoteAddress;
    }

}
