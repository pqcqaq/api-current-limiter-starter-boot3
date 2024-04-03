package online.zust.qcqcqc.utils.aspects;

import online.zust.qcqcqc.utils.annotation.CurrentLimit;
import online.zust.qcqcqc.utils.config.condition.LimitAspectCondition;
import online.zust.qcqcqc.utils.entity.Limiter;
import online.zust.qcqcqc.utils.exception.ApiCurrentLimitException;
import online.zust.qcqcqc.utils.exception.ErrorTryAccessException;
import online.zust.qcqcqc.utils.limiters.CurrentLimiterManager;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
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
@Order(3)
public class CurrentLimitAspect {

    private static final Logger log = LoggerFactory.getLogger(CurrentLimitAspect.class);

    private CurrentLimiterManager currentLimiterManager;

    @Autowired
    public void setCurrentLimiterManager(CurrentLimiterManager currentLimiterManager) {
        this.currentLimiterManager = currentLimiterManager;
    }


    @Pointcut("@annotation(online.zust.qcqcqc.utils.annotation.CurrentLimit)")
    private void check() {
    }

    @Before("check()")
    public void before(JoinPoint joinPoint) {
        log.debug("使用：{}，进行限流", currentLimiterManager.getClass().getSimpleName());
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        CurrentLimit limit = method.getAnnotation(CurrentLimit.class);

        String key = limit.key().trim();
        if (key.isEmpty()) {
            key = method.getName();
        }
        Limiter limiter = Limiter.builder().limitNum(limit.limitNum())
                .seconds(limit.seconds())
                .key(key)
                .limitByUser(limit.limitByUser())
                .isBefore(false)
                .build();

        boolean b;
        try {
            b = currentLimiterManager.tryAccess(limiter);
        } catch (Exception e) {
            log.error("限流器：{}，发生异常：{}", currentLimiterManager.getClass().getSimpleName(), e.getMessage());
            throw new ErrorTryAccessException(e.getMessage());
        }
        if (!b) {
            log.warn("接口：{}，已被限流  key：{}，在{}秒内访问次数超过{}，限流类型：{}",
                    method.getName(), key, limiter.getSeconds(),
                    limiter.getLimitNum(),
                    limiter.isLimitByUser() ? "用户限流" : "全局限流");
            throw new ApiCurrentLimitException(limit.msg());
        }
    }
}
