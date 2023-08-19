package com.qgstudio.MyLogger;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import cn.hutool.json.JSONUtil;
import com.qgstudio.websocket.WebsocketClient;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.*;


@Component
public class MyLogger {
    private final Logger LOGGER = ((Logger) LoggerFactory.getLogger("MyLog.class"));

    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    private String url = "http://localhost:8080/logger";


    private final ListAppender<ILoggingEvent> listAppender = new ListAppender<>();


    private WebsocketClient websocketClient;

    public MyLogger() {
        try {
            //记录日志框架中
            listAppender.setContext(((LoggerContext) LoggerFactory.getILoggerFactory()));
            listAppender.start();
            LOGGER.addAppender(listAppender);

            //websocket与服务端建立连接，将日志信息发送
            this.websocketClient = new WebsocketClient(url);
            timer();

        } catch (Exception e) {
            e.printStackTrace();
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


    public void error(String message) {
        LOGGER.error(message);
        sendMessage();
    }

    public void trace(String message) {
        LOGGER.trace(message);
    }

    private void timer() {
        //开启定时器，定时将日志信息传到服务端
        ScheduledExecutorService pool = new ScheduledThreadPoolExecutor(10, Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        pool.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendMessage();
            }
        }, 10, 10, TimeUnit.SECONDS);
    }


    //将消息发送到服务端
    public synchronized void sendMessage() {
        try {
            List<Log> logList = new ArrayList<Log>(listAppender.list.size());
            if (!websocketClient.isOpen()) {
                websocketClient = new WebsocketClient(url);
                websocketClient.connect();
            }

            listAppender.list.forEach(event -> {
                Log log = new Log();
                log.setModuleName(event.getLoggerName());
                log.setTime(format.format(event.getTimeStamp()));
                log.setMessage(event.getFormattedMessage());
                log.setThread(event.getThreadName());
                log.setLevel(event.getLevel().toString());
                logList.add(log);
            });
            websocketClient.send(JSONUtil.toJsonStr(logList));
            System.out.println(JSONUtil.toList(JSONUtil.toJsonStr(logList), Log.class));
            listAppender.list.clear();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
