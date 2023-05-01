//package com.vd.backend.aop;
//
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Before;
//import org.springframework.stereotype.Component;
//
//
///**
// * use to clean and adopt the front end data
// */
//@Aspect
//@Component
//public class FhirResourceAspect {
//
//    @Before("execution(* com.vd.backend.controller.FhirController.*(..))")
//    public void resourceFormatter(ProceedingJoinPoint joinPoint) {
//        Object[] args = joinPoint.getArgs();
//
//    }
//
//}
