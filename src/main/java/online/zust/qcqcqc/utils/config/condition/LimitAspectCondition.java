package online.zust.qcqcqc.utils.config.condition;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * @author pqcmm
 * 限流功能开启条件
 * 通过配置文件中是否包含limiter.enable来判断是否开启限流功能
 */
public class LimitAspectCondition implements Condition {

    private static final Logger log = LoggerFactory.getLogger(LimitAspectCondition.class);

    @Override
    public boolean matches(ConditionContext conditionContext, AnnotatedTypeMetadata annotatedTypeMetadata) {
        //检查配置文件是否包含limit.enable
        boolean b = Boolean.parseBoolean(conditionContext.getEnvironment().getProperty("limiter.enable"));
        log.info(b ? "限流功能已开启!" : "限流功能已关闭~");
        return b;
    }
}
