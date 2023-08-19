package com.qgstudio.websocket;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.Closeable;
import java.io.IOException;

public abstract class AbstractWebsocketClient implements Closeable {

    public void send(String message)  {
        Channel channel = getChannel();
        channel.writeAndFlush(new TextWebSocketFrame(message));

    }
    public void connect() throws Exception {
        try {
            doOpen();
            doConnect();
        } catch (Exception e) {
            throw new Exception("连接没有成功打开,原因是:{}" + e.getMessage(), e);
        }
    }

    protected abstract void doOpen();

    protected abstract void doConnect() throws Exception;

    protected abstract Channel getChannel();


    protected abstract void shakeHand() throws InterruptedException;

    @Override
    public abstract void close()  ;
}
