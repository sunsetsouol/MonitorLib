package com.qgstudio.annotation;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.json.JSONUtil;
import com.qgstudio.MyLogger.MyLogger;
import com.qgstudio.websocket.WebsocketClient;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import javax.servlet.http.HttpServletRequest;
import java.util.TimerTask;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Aspect
public class ModuleApplication {



    private  String url = "http://localhost:8080/situation";

    private  WebsocketClient websocketClient;

    public ModuleApplication() {
        timer();
        websocketClient = new WebsocketClient(url);
    }

    @Autowired
    private MyLogger myLogger;


    private  ConcurrentLinkedDeque<SituationLogs> situations = new ConcurrentLinkedDeque<>();

    private  ConcurrentHashMap<String, AtomicInteger> ipAddresses = new ConcurrentHashMap<>();

    private ConcurrentHashSet<String> ips = new ConcurrentHashSet<>();


    private void timer() {
        ScheduledExecutorService pool = new ScheduledThreadPoolExecutor(10, Executors.defaultThreadFactory(), new ThreadPoolExecutor.AbortPolicy());
        //开启定时器，定时将日志信息传到服务端
        pool.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendMessage();
            }
        }, 10, 10, TimeUnit.SECONDS);

        //将记录清零
        pool.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ipAddresses.forEach((key, value) ->{
                    value.set(0);
                });
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    private  void sendMessage() {
        try {
            if (!websocketClient.isOpen()) {
                websocketClient = new WebsocketClient(url);
                websocketClient.connect();
            }
            System.out.println(JSONUtil. toJsonStr(situations));
            websocketClient.send(JSONUtil.toJsonStr(situations));
            situations.clear();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Pointcut("@annotation(com.qgstudio.annotation.Module)")
    public void pt(){}

    /**
     * 获取用户访问方法的ip，判断是否非法操作，如果访问次数过多则会抛出异常
     */
    @Before("pt()")
    public void before(){
        //获取ip地址
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        String ipAddress = getIp(request);
        //判断是否锁定ip地址
        if (ips.contains(ipAddress)){
            throw new RuntimeException(ipAddress + "锁定的ip地址");
        }
        //是否第一次访问
        if (ipAddresses.get(ipAddress) == null) {
            //第一次访问，put进容器
            ipAddresses.put(ipAddress, new AtomicInteger(1));
        } else {
            //不是第一次访问，访问次数+1
            ipAddresses.get(ipAddress).incrementAndGet();
            //访问次数过多报错
            if (ipAddresses.get(ipAddress).get() > 100) {
                //通过日志报错记录ip地址并抛出异常
//                myLogger.error(ipAddress + "非法攻击");
                ips.add(ipAddress);
                throw new RuntimeException(ipAddress + "非法攻击");
            }
        }

    }

    /**
     * 获取request的ip地址
     * @param request request请求
     * @return ip地址
     */
    public static String getIp(HttpServletRequest request){
        String ipAddress = request.getHeader("X-Forwarded-For");
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if(ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)){
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)){
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)){
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)){
            ipAddress = request.getRemoteAddr();
        }
        return ipAddress;
    }



    //正常返回操作日志
    @AfterReturning(value = "pt()", returning = "ret")
    public void after(JoinPoint joinPoint, Object ret) {
        String methodName = joinPoint.getSignature().getName();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class methodReturnClass = signature.getReturnType();

        situations.add(new SituationLogs(methodName,getArrayString(signature.getParameterTypes()),
                getArrayString(joinPoint.getArgs()),methodReturnClass == null ? "" : methodReturnClass.getName(),
                ret == null ? "" : ret.getClass().getName(),0));
    }

    //添加异常操作日志
    @AfterThrowing(value = "pt()", throwing = "t")
    public void thr(JoinPoint joinPoint, Throwable t) {
        String methodName = joinPoint.getSignature().getName();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class methodReturnClass = signature.getReturnType();
        situations.add(new SituationLogs(methodName,getArrayString(signature.getParameterTypes()),
                getArrayString(joinPoint.getArgs()),methodReturnClass == null ? "" : methodReturnClass.getName()
                ,t.getClass().getName(),1));
        sendMessage();
    }


    //拼接类数组返回字符串
    public String getArrayString(Object[] args) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (Object arg : args) {
            stringBuilder.append(arg.getClass().getName()).append(",");
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }
}
