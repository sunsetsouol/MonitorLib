package com.qgstudio;

import io.netty.channel.*;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.EventExecutorGroup;

public class WebsocketClientHandler extends SimpleChannelInboundHandler<Object> {


    /**
     * 连接处理器
     */
    private final WebSocketClientHandshaker webSocketClientHandshaker;

    /**
     * netty提供的数据过程中的数据保证
     */
    private ChannelPromise handshakeFuture;

    private Channel channel;

    public WebsocketClientHandler(WebSocketClientHandshaker webSocketClientHandshaker) {
        this.webSocketClientHandshaker = webSocketClientHandshaker;
    }

    public ChannelFuture handshakeFuture() {
        return handshakeFuture;
    }

    /**
     * ChannelHandler添加到实际上下文中准备处理事件,调用此方法
     *
     * @param ctx ChannelHandlerContext
     */

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        handshakeFuture = ctx.newPromise();
    }

    /**
     * 当客户端主动链接服务端的链接后,调用此方法
     *
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        channel = ctx.channel();
        webSocketClientHandshaker.handshake(channel);
        System.out.println("建立连接");
    }

    /**
     * 链接断开后,调用此方法
     *
     * @param ctx ChannelHandlerContext
     */
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println("连接断开");
    }

    /**
     * 接收消息,调用此方法
     *
     * @param ctx ChannelHandlerContext
     * @param msg Object
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (!webSocketClientHandshaker.isHandshakeComplete()) {
            this.handleHttpRequest(msg);
            System.out.println("websocket已经建立连接");
            return;
        }
        if (msg instanceof FullHttpResponse) {
            FullHttpResponse response = (FullHttpResponse) msg;
            throw new IllegalStateException("Unexpected FullHttpResponse (getStatus=" + response.status() + ", content=" + response.content().toString(CharsetUtil.UTF_8) + ')');
        }
        this.handleWebSocketFrame(msg);
    }

    /**
     * 处理http连接请求.<br>
     *
     * @param msg:
     */
    private void handleHttpRequest(Object msg) {
        webSocketClientHandshaker.finishHandshake(channel, (FullHttpResponse) msg);
        handshakeFuture.setSuccess();
    }

    /**
     * 处理文本帧请求.<br>
     *
     * @param msg 消息
     */
    private void handleWebSocketFrame(Object msg) {
        WebSocketFrame frame = (WebSocketFrame) msg;
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;
            // ...自定义
            System.out.println("收到消息：{}"+ textFrame.text());
        } else if (frame instanceof CloseWebSocketFrame) {
            System.out.println("连接收到关闭帧");
            channel.close();
        }
    }

    /**
     * 运行过程中未捕获的异常,调用此方法
     *
     * @param ctx ChannelHandlerContext
     * @param cause Throwable
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("监控触发异常=>{}"+ cause.getMessage()+ cause);
    }

//    private WebSocketClientHandshaker webSocketClientHandshaker;
//
//    private ChannelPromise handshakeFuture;
//
//    private Channel channel;
//    public WebsocketClientHandler(WebSocketClientHandshaker webSocketClientHandshaker) {
//        this.webSocketClientHandshaker = webSocketClientHandshaker;
//    }
//
//    public ChannelFuture handshakeFuture() {
//        return handshakeFuture;
//    }
//
//    @Override
//    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
//        handshakeFuture=ctx.newPromise();
//    }
//
//    @Override
//    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
//        System.out.println("连接断开");
//    }
//
//
//    @Override
//    public void channelActive(ChannelHandlerContext ctx) throws Exception {
//        channel=ctx.channel();
//        webSocketClientHandshaker.handshake(channel);
//        System.out.println("建立连接");
//    }
//
//    @Override
//    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
//        if (!webSocketClientHandshaker.isHandshakeComplete()){
//            this.handleHttpRequest(msg);
//            System.out.println("websocket已经建立连接");
//            return;
//        }
//        WebSocketFrame frame = (WebSocketFrame) msg;
//        if (frame instanceof TextWebSocketFrame){
//            TextWebSocketFrame testFrame = (TextWebSocketFrame) frame;
//            System.out.println("收到消息" + testFrame.text());
//        }else {
//            if (frame instanceof CloseWebSocketFrame){
//                System.out.println("收到关闭帧");
//                channel.close();
//            }
//        }
//    }
//
//    private void handleHttpRequest(Object msg) {
//        webSocketClientHandshaker.finishHandshake(channel, ((FullHttpResponse) msg));
//        handshakeFuture.setSuccess();
//    }
}
