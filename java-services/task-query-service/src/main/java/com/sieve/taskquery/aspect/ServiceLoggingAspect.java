package com.sieve.taskquery.aspect;

import com.sieve.taskquery.annotation.AuditedService;
import com.sieve.taskquery.annotation.TimedExecution;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
@Component
public class ServiceLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(ServiceLoggingAspect.class);

    @Around("@annotation(com.sieve.taskquery.annotation.TimedExecution)")
    public Object time(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            long elapsedMillis = (System.nanoTime() - start) / 1_000_000;
            log.info("method={} executionMs={}", joinPoint.getSignature().toShortString(), elapsedMillis);
        }
    }

    @Around("@within(com.sieve.taskquery.annotation.AuditedService)")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("enter method={}", joinPoint.getSignature().toShortString());
        try {
            return joinPoint.proceed();
        } finally {
            log.info("exit method={}", joinPoint.getSignature().toShortString());
        }
    }
}
