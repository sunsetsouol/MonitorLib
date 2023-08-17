package com.qgstudio;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;

import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketClient extends AbstractWebsocketClient{
    private static final NioEventLoopGroup NIO_GROUP = new NioEventLoopGroup();

    private final URI uri;

    private final int port;

    private Bootstrap bootstrap;

    private WebsocketClientHandler handler;

    private Channel channel;

    public WebsocketClient(String url) throws URISyntaxException, Exception {
        super();
        this.uri = new URI(url);
        this.port = getPort();
    }

    /**
     * Extract the specified port
     *
     * @return the specified port or the default port for the specific scheme
     */
    private int getPort() throws Exception {
        int port = uri.getPort();
        if (port == -1) {
            String scheme = uri.getScheme();
            System.out.println("scheme===="+scheme);
            if ("wss".equals(scheme)) {
                return 443;
            } else if ("ws".equals(scheme)) {
                return 80;
            } else {
                throw new Exception("unknown scheme: " + scheme);
            }
        }
        return port;
    }

    @Override
    protected void doOpen() {
        // websocket客户端握手实现的基类
        WebSocketClientHandshaker webSocketClientHandshaker = WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders());
        // 业务处理类
        handler = new WebsocketClientHandler(webSocketClientHandshaker);
        // client端，引导client channel启动
        bootstrap = new Bootstrap();
        // 添加管道 绑定端口 添加作用域等
        bootstrap.group(NIO_GROUP).channel(NioSocketChannel.class).handler(new WebsocketChannelInitializer(handler));
    }

    @Override
    protected void doConnect() {
        try {
            System.out.println(uri.getHost());
            System.out.println(port);
            // 启动连接
            channel = bootstrap.connect(uri.getHost(), port).sync().channel();
            // 等待握手响应
            handler.handshakeFuture().sync();
            System.out.println("after handshake");
        } catch (InterruptedException e) {
            System.out.println("websocket连接发生异常"+ e);
            Thread.currentThread().interrupt();
        }
    }

    @Override
    protected Channel getChannel() {
        return channel;
    }

    @Override
    public void close() {
        if (channel != null) {
            System.out.println("close");
            channel.close();
        }
    }

    @Override
    protected void shakeHand() throws InterruptedException {
        handler.handshakeFuture().sync();
    }

    public boolean isOpen() {
        return channel.isOpen();
    }


//    private static URI uri;
//
//    private static final NioEventLoopGroup NIO_GROUP = new NioEventLoopGroup();
//
//    private Bootstrap bootstrap;
//    private Channel channel;
//    static WebsocketClientHandler handler = new WebsocketClientHandler(WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()));
//    public void start() throws InterruptedException, URISyntaxException {
//        EventLoopGroup workerGroup = new NioEventLoopGroup();
//
//        bootstrap = new Bootstrap().group(workerGroup)
//                .channel(NioSocketChannel.class)
//                .option(ChannelOption.TCP_NODELAY, true)
//                .option(ChannelOption.SO_KEEPALIVE, true)
//                .handler(new ChannelInitializer<SocketChannel>() {
//                    @Override
//                    public void initChannel(SocketChannel socketChannel) throws Exception {
//                        ChannelPipeline pipeline = socketChannel.pipeline();
//                        pipeline.addLast(new HttpClientCodec());
//                        pipeline.addLast(new HttpObjectAggregator(8192));
//                        pipeline.addLast(WebSocketClientCompressionHandler.INSTANCE);
//                        pipeline.addLast(handler);
//                    }
//                });
//        uri = new URI("http://localhost:8081/websocket");
//        channel = bootstrap.connect(uri.getHost(), 8081).sync().channel();
//        handler.handshakeFuture().sync();
//
//
//
//    }
//
//    @Override
//    protected void doOpen() {
//        // websocket客户端握手实现的基类
//        WebSocketClientHandshaker webSocketClientHandshaker = WebSocketClientHandshakerFactory.newHandshaker(uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders());
//        // 业务处理类
//        handler = new WebsocketClientHandler(webSocketClientHandshaker);
//        // client端，引导client channel启动
//        bootstrap = new Bootstrap();
//        // 添加管道 绑定端口 添加作用域等
//        bootstrap.group(NIO_GROUP).channel(NioSocketChannel.class).handler(new WebsocketChannelInitializer(handler));
//
//    }
//
//    @Override
//    protected void doConnect() throws Exception {
//        try {
//            // 启动连接
//            channel = bootstrap.connect(uri.getHost(), port).sync().channel();
//            // 等待握手响应
//            handler.handshakeFuture().sync();
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
//    }
//
//    @Override
//    protected Channel getChannel() {
//        return null;
//    }
//
//    @Override
//    public void close() {
//
//    }

}
