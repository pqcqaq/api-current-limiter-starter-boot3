package online.zust.qcqcqc.utils.config.condition;

import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.ConfigurationCondition;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author qcqcqc
 * 全局限流功能开启条件
 * 通过配置文件中是否包含limiter.global.enable来判断是否开启全局限流功能
 */
public class GlobalLimitInterceptorCondition implements ConfigurationCondition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        // 获取 limiter.global.enable 的值
        return Boolean.parseBoolean(context.getEnvironment().getProperty("limiter.global.enable"));
    }

    @Override
    public ConfigurationPhase getConfigurationPhase() {
        return ConfigurationPhase.REGISTER_BEAN;
    }
}
