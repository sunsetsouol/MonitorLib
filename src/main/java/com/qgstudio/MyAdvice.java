package com.qgstudio;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;


//@Aspect
public class MyAdvice {

    @Pointcut("execution(* org.slf4j.Logger.*(..))")
//    @Pointcut("execution(* com.qgstudio.A.a())")
    public void pt(){
    }

    @Before("pt()")
    public void before(){
        System.out.println("before.......");
    }

    @After("pt()")
    public void after(){
        System.out.println("after......");
    }

    @Around("pt()")
    public Object method(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        System.out.println("around start .....");
        Object proceed = proceedingJoinPoint.proceed();
        System.out.println("around end........");
        return proceed;
    };

    @AfterReturning(value = "pt()",returning = "ret")
    public void af(Object ret){
        System.out.println("aftering returning");
    };

    @AfterThrowing(value = "pt()",throwing = "t")
    public void thr(Throwable t){
        System.out.println("aftering throwing");
    };
}
