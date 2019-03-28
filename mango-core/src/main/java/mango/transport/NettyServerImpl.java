package mango.transport;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import mango.common.URL;
import mango.common.URLParam;
import mango.core.DefaultRequest;
import mango.core.DefaultResponse;
import mango.exception.RpcFrameworkException;
import mango.rpc.MessageRouter;
import mango.rpc.RpcContext;
import mango.util.Constants;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ${DESCRIPTION}
 * Netty服务器实现类
 * @author Ricky Fung
 */
public class NettyServerImpl extends AbstractServer {
    /**
     * NioEventLoopGroup第一个通常称为“boss”，接受传入连接。 第二个通常称为“worker”，
     * 当“boss”接受连接并且向“worker”注册接受连接，则“worker”处理所接受连接的流量
     */
    private EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private EventLoopGroup workerGroup = new NioEventLoopGroup();
    /**
     * 用于设置服务器的助手类
     */
    private ServerBootstrap serverBootstrap = new ServerBootstrap();
    //业务处理线程池
    private ThreadPoolExecutor pool;
    // 消息处理路由
    private MessageRouter router;
    // 是否初始化
    private volatile boolean initializing = false;

    public NettyServerImpl(URL url, MessageRouter router){
        super(url);

        this.localAddress = new InetSocketAddress(url.getPort());
        this.router = router;
        this.pool = new ThreadPoolExecutor(url.getIntParameter(URLParam.minWorkerThread.getName(), URLParam.minWorkerThread.getIntValue()),
                url.getIntParameter(URLParam.maxWorkerThread.getName(), URLParam.maxWorkerThread.getIntValue()),
                120, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
                new DefaultThreadFactory(String.format("%s-%s", Constants.FRAMEWORK_NAME, "biz")));
    }

    @Override
    public synchronized boolean open() {
        if(initializing) {
            logger.warn("NettyServer ServerChannel is initializing: url=" + url);
            return true;
        }
        initializing = true;

        if (state.isAvailable()) {
            logger.warn("NettyServer ServerChannel has initialized: url=" + url);
            return true;
        }
        // 最大响应包限制
        final int maxContentLength = url.getIntParameter(URLParam.maxContentLength.getName(),
                URLParam.maxContentLength.getIntValue());

        this.serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_RCVBUF, url.getIntParameter(URLParam.bufferSize.getName(), URLParam.bufferSize.getIntValue()))
                .childOption(ChannelOption.SO_SNDBUF, url.getIntParameter(URLParam.bufferSize.getName(), URLParam.bufferSize.getIntValue()))
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    public void initChannel(SocketChannel ch) throws IOException {
                        ch.pipeline().addLast(new NettyDecoder(codec, url, maxContentLength, Constants.HEADER_SIZE, 4),
                                new NettyEncoder(codec, url),
                                // Netty服务端消息处理器
                                new NettyServerHandler());
                    }
                });

        try {
            ChannelFuture channelFuture = this.serverBootstrap.bind(this.localAddress).sync();

            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture f) throws Exception {

                    if(f.isSuccess()){
                        logger.info("Rpc Server bind port:{} success", url.getPort());
                    } else {
                        logger.error("Rpc Server bind port:{} failure", url.getPort());
                    }
                }
            });
        } catch (InterruptedException e) {
            logger.error(String.format("NettyServer bind to address:%s failure", this.localAddress), e);
            throw new RpcFrameworkException(String.format("NettyClient connect to address:%s failure", this.localAddress), e);
        }
        state = ChannelState.AVAILABLE;
        return true;
    }

    @Override
    public boolean isAvailable() {
        return state.isAvailable();
    }

    @Override
    public boolean isClosed() {
        return state.isClosed();
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public void close() {
        close(0);
    }

    @Override
    public synchronized void close(int timeout) {

        if (state.isClosed()) {
            logger.info("NettyServer close fail: already close, url={}", url.getUri());
            return;
        }

        try {
            this.bossGroup.shutdownGracefully();
            this.workerGroup.shutdownGracefully();
            this.pool.shutdown();

            state = ChannelState.CLOSED;
        } catch (Exception e) {
            logger.error("NettyServer close Error: url=" + url.getUri(), e);
        }
    }

    /**
     * Netty服务端消息处理器
     */
    class NettyServerHandler extends SimpleChannelInboundHandler<DefaultRequest> {

        @Override
        protected void channelRead0(ChannelHandlerContext context, DefaultRequest request) throws Exception {

            logger.info("Rpc server receive request id:{}", request.getRequestId());
            //处理请求，每收到一个request请求就启动一个线程去处理
            processRpcRequest(context, request);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            // 异常处理
            logger.error("NettyServerHandler exceptionCaught: remote=" + ctx.channel().remoteAddress() + " local=" + ctx.channel().localAddress(), cause);
            ctx.channel().close();
        }
    }

    /**处理客户端请求**/
    private void processRpcRequest(final ChannelHandlerContext context, final DefaultRequest request) {
        final long processStartTime = System.currentTimeMillis();
        try {
            // 启动一个线程去处理该请求
            this.pool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        // 初始化RPC上下文
                        RpcContext.init(request);
                        processRpcRequest(context, request, processStartTime);
                    } finally {
                        // 销毁此RPC上下文
                        RpcContext.destroy();
                    }

                }
            });
        } catch (RejectedExecutionException e) {
            // 构建一个异常的response
            DefaultResponse response = new DefaultResponse();
            response.setRequestId(request.getRequestId());
            response.setException(new RpcFrameworkException("process thread pool is full, reject"));
            response.setProcessTime(System.currentTimeMillis() - processStartTime);
            context.channel().write(response);
        }

    }
    /** 处理rpc请求 **/
    private void processRpcRequest(ChannelHandlerContext context, DefaultRequest request, long processStartTime) {
        // 将request拿到消息路由中去处理，返回默认的response实体
        DefaultResponse response = (DefaultResponse) this.router.handle(request);
        // 设置整个请求的时间
        response.setProcessTime(System.currentTimeMillis() - processStartTime);
        //非单向调用
        if(request.getType()!=Constants.REQUEST_ONEWAY){
            // 将response写入到通道中
            context.writeAndFlush(response);
        }
        logger.info("Rpc server process request:{} end...", request.getRequestId());
    }
}
