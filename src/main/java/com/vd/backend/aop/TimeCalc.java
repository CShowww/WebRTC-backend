package com.vd.backend.aop;


import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;


@Aspect
@Component
@Slf4j
public class TimeCalc {

    @Pointcut("execution(* com.vd.backend.service.impl.AsyncFhirServiceImpl.*(..)) " +
            "|| execution(* com.vd.backend.service.impl.AppointmentServiceImpl.*(..)) " +
            "|| execution(* com.vd.backend.service.impl.ObservationServiceImpl.*(..)) " +
            "|| execution(* com.vd.backend.service.impl.ProfilesServiceImpl.*(..))" +
            "|| execution(* com.vd.backend.service.impl.PrescriptionServiceImpl.*(..))")

//    @Pointcut("execution(* com.vd.backend.service.impl.*.*(..))")
    public void methodPointCut() {
    }

    @Around("methodPointCut()")
    public Object runTimeStatistics(ProceedingJoinPoint pjp) throws Throwable {
        Signature signature = pjp.getSignature();

        String className = pjp.getTarget().getClass().getName();

        String methodName = signature.getName();

        Object[] requestParams = pjp.getArgs();
        StringBuffer sb = new StringBuffer();
        for(Object requestParam : requestParams){
            if(requestParam!=null){
                sb.append(JSON.toJSONString(requestParam));
                sb.append(",");
            }
        }
        String requestParamsString = sb.toString();
        if(requestParamsString.length()>0){
            requestParamsString = requestParamsString.substring(0, requestParamsString.length() - 1);
        }

        long start = System.currentTimeMillis();

        Object response = pjp.proceed();

        long end = System.currentTimeMillis();

        log.info("{}.{} param: {}; Time consume: {}ms", className, methodName, requestParamsString, end - start);


        return response;
    }
}