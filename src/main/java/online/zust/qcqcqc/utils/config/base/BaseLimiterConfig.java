package online.zust.qcqcqc.utils.config.base;

import jakarta.servlet.http.HttpServletRequest;
import online.zust.qcqcqc.utils.config.LimiterConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;


/**
 * @author qcqcqc
 */
@Configuration
@ConditionalOnMissingBean(LimiterConfig.class)
public class BaseLimiterConfig implements LimiterConfig {

    @Value("${limiter.remote-info.user-key:X-Forwarded-For}")
    private String headerKey;
    @Value("${limiter.remote-info.use-proxy:false}")
    private Boolean useProxy;

    /**
     * 请求上下文
     */
    private HttpServletRequest httpServletRequest;

    @Autowired
    public void setHttpServletRequest(HttpServletRequest httpServletRequest) {
        this.httpServletRequest = httpServletRequest;
    }

    @Override
    public String getUserKey() {
        /*
         * 从请求上下文中获取用户的唯一标识
         */
        if (useProxy) {
            return httpServletRequest.getHeader(headerKey);
        } else {
            return httpServletRequest.getRemoteAddr();
        }
    }
}
