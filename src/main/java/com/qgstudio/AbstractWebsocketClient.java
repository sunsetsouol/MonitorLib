package com.qgstudio;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

import java.io.Closeable;
import java.io.IOException;

public abstract class AbstractWebsocketClient implements Closeable {

    public void send(String message) throws Exception {
        System.out.println("send");
        Channel channel = getChannel();
        if (!channel.isActive()){
            System.out.println("reshake");
            connect();
        }

        System.out.println("write");
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
