package cn.kmbeast.aop;

import cn.kmbeast.security.AccessPolicy;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ProtectorAspect {
    @Around("@annotation(protector)")
    public Object auth(ProceedingJoinPoint joinPoint, Protector protector) throws Throwable {
        AccessPolicy.requireRoles(protector.roles());
        // Request context belongs to the interceptor, including cleanup after failures.
        return joinPoint.proceed();
    }
}
