package com.qgstudio;



import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;

import org.slf4j.LoggerFactory;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        Logger logger = (Logger) LoggerFactory.getLogger(Main.class);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.setContext(((LoggerContext) LoggerFactory.getILoggerFactory()));
        listAppender.start();
        logger.addAppender(listAppender);
        logger.info("hello");
        listAppender.list.forEach(event ->{
            System.out.println(event.getLoggerName());
        });

//        try (WebsocketClient websocketClient = new WebsocketClient("http://localhost:8081/websocket1")) {
//            // 连接
//            websocketClient.connect();
//            // 发送消息
//            websocketClient.send("xxxxxxxxxxxxxxxxx");
//            Scanner scanner = new Scanner(System.in);
//            while (true){
//                String next = scanner.next();
//                if ("exit".equals(next)){
//                    break;
//                }
//                System.out.println(websocketClient == null);
//                websocketClient.send(next);
//            }
//            if (scanner.next() == "go"){
//                websocketClient.send("go");
//            }
//            // 阻塞一下，否则这里客户端会调用close方法
//            Thread.sleep(10);
//            System.out.println("after sleep");
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }
}
