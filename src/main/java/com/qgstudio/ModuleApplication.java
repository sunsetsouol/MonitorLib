package com.qgstudio;

import com.qgstudio.annotation.Module;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


@Aspect
public class ModuleApplication {

    private static ConcurrentHashMap<String , AtomicInteger> searcher = new ConcurrentHashMap<String ,AtomicInteger>();

//    @Pointcut("execution(void com.qgstudio.MyLog.a())")
    @Pointcut("@annotation(com.qgstudio.annotation.Module)")
    public void pt(){}

    @Before("pt()")
    public void before(){
        System.out.println("before......");
    }


//    @Around("@annotation(com.qgstudio.annotation.Module)")
    @Around("pt()")
    public Object method(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
//        System.out.println("before................................................................");
//        Module annotation = ((MethodSignature) proceedingJoinPoint.getSignature()).getMethod().getAnnotation(Module.class);
//        if (searcher.get(annotation.value())== null) {
//            searcher.put(annotation.value(),new AtomicInteger(0));
//        }
        Object proceed = proceedingJoinPoint.proceed();
//        searcher.get(annotation.value()).incrementAndGet();
        System.out.println("after................................................................");
        return proceed;
    }


}
