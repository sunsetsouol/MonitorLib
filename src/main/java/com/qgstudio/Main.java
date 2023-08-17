package com.qgstudio;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        MyLogger myLogger = new MyLogger();
        Scanner scanner = new Scanner(System.in);
        while (true){
            myLogger.info(scanner.nextLine());
        }

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
