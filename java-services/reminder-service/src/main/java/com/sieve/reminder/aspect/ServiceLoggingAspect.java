package com.sieve.reminder.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ServiceLoggingAspect {

    private static final Logger log = LoggerFactory.getLogger(ServiceLoggingAspect.class);

    @Around("@annotation(com.sieve.reminder.annotation.TimedExecution)")
    public Object time(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            log.info("method={} executionMs={}", joinPoint.getSignature().toShortString(),
                    (System.nanoTime() - start) / 1_000_000);
        }
    }

    @Around("@within(com.sieve.reminder.annotation.AuditedService)")
    public Object audit(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("enter method={}", joinPoint.getSignature().toShortString());
        try {
            return joinPoint.proceed();
        } finally {
            log.info("exit method={}", joinPoint.getSignature().toShortString());
        }
    }
}
