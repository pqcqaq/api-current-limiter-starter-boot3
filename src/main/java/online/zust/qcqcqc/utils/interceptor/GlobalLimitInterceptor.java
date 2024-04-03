package online.zust.qcqcqc.utils.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import online.zust.qcqcqc.utils.entity.Limiter;
import online.zust.qcqcqc.utils.exception.ApiCurrentLimitException;
import online.zust.qcqcqc.utils.exception.ErrorTryAccessException;
import online.zust.qcqcqc.utils.limiters.CurrentLimiterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

/**
 * @author qcqcqc
 */
public class GlobalLimitInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(GlobalLimitInterceptor.class);

    @Autowired
    private CurrentLimiterManager currentLimiterManager;
    @Value("${limiter.global.limit-num:100}")
    private Integer limitNum;
    @Value("${limiter.global.seconds:10}")
    private Integer seconds;
    @Value("${limiter.global.on-method:true}")
    private Boolean onMethod;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.debug("使用：{}，进行限流", currentLimiterManager.getClass().getSimpleName());
        if (handler instanceof HandlerMethod h) {
            Method method = h.getMethod();

            String key;
            if (onMethod) {
                key = "global-limit-" + method.getName();
            } else {
                key = "global-limit";
            }
            Limiter limiter = Limiter.builder().limitNum(limitNum)
                    .seconds(seconds)
                    .key(key)
                    .limitByUser(false)
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
                        "全局限流");
                throw new ApiCurrentLimitException("there are too many people accessing the service, please try again later");
            }
        }
        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
