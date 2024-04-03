package online.zust.qcqcqc.utils.aspects;

import online.zust.qcqcqc.utils.annotation.ConcurrentLimit;
import online.zust.qcqcqc.utils.config.condition.LimitAspectCondition;
import online.zust.qcqcqc.utils.entity.Limiter;
import online.zust.qcqcqc.utils.exception.ApiCurrentLimitException;
import online.zust.qcqcqc.utils.limiters.ConcurrentLimiterManager;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.annotation.Order;

import java.lang.reflect.Method;

/**
 * @author pqcmm
 * 使用CGLIB代理
 */
@Aspect
@EnableAspectJAutoProxy(proxyTargetClass = true)
@Conditional(LimitAspectCondition.class)
@Order(1)
public class ConcurrentLimitAspect {

    private static final Logger log = LoggerFactory.getLogger(ConcurrentLimitAspect.class);

    private ConcurrentLimiterManager concurrentLimiterManager;

    @Autowired
    public void setConcurrentLimiterManager(ConcurrentLimiterManager concurrentLimiterManager) {
        this.concurrentLimiterManager = concurrentLimiterManager;
    }

    @Pointcut("@annotation(online.zust.qcqcqc.utils.annotation.ConcurrentLimit)")
    private void access() {
    }

    @Around("access()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();

        ConcurrentLimit limit = method.getAnnotation(ConcurrentLimit.class);
        // 在处理之前检查访问记录
        Limiter build = Limiter.builder().limitNum(limit.limitNum())
                .limitByUser(limit.limitByUser())
                .key(limit.key())
                .isBefore(true)
                .build();
        boolean b = concurrentLimiterManager.tryAccess(build);
        if (!b) {
            log.warn("接口：{}，已被限流  key：{}，并发数超过{}，限流类型：{}",
                    method.getName(), limit.key(), limit.limitNum(),
                    limit.limitByUser() ? "用户限流" : "全局限流");
            throw new ApiCurrentLimitException("当前访问人数过多，请稍后再试");
        }
        try {
            return proceedingJoinPoint.proceed();
        } finally {
            // 在处理之后移除访问记录
            build.setBefore(false);
            concurrentLimiterManager.tryAccess(build);
        }
    }
}
