package com.qgstudio;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;
import java.util.concurrent.*;


public class MyLogger {
    private final Logger LOGGER = ((Logger) LoggerFactory.getLogger("MyLog.class"));


    private final ListAppender<ILoggingEvent> listAppender = new ListAppender<>();


    private WebsocketClient websocketClient ;

    public MyLogger( ) {
        try {
            //记录日志框架中
            listAppender.setContext(((LoggerContext) LoggerFactory.getILoggerFactory()));
            listAppender.start();
            LOGGER.addAppender(listAppender);

            //websocket与服务端建立连接，将日志信息发送
            this.websocketClient =  new WebsocketClient("http://localhost:8081/websocket");
            this.websocketClient.connect();
            timer();

        } catch (Exception e) {
            throw new RuntimeException("WebsocketClient 初始化失败");
        }

    }

    //重写日志方法
    public void info(String message) {
        LOGGER.info(message);
    }

    public void debug(String message) {
        LOGGER.debug(message);
    }

    public void warn(String message) {
        LOGGER.warn(message);
    }


    public  void error(String message) {
        LOGGER.error(message);
    }

    public  void trace(String message) {
        LOGGER.trace(message);
    }

    private void timer(){
        //开启定时器，定时将日志信息传到服务端
        ScheduledExecutorService pool = new ScheduledThreadPoolExecutor(10, Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());
        pool.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendMessage();
            }
        },10,10, TimeUnit.SECONDS);
    }


    //将消息发送到服务端
    public synchronized void sendMessage() {
        try {
            listAppender.list.forEach(event -> {
                try {
                    System.out.println("message="+event.getMessage());
                    if (!websocketClient.isOpen()){
                        websocketClient = new WebsocketClient("http://localhost:8081/websocket");
                        websocketClient.connect();
                    }
                    websocketClient.send(event.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            listAppender.list.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
