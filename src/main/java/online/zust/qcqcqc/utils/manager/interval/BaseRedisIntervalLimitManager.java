package online.zust.qcqcqc.utils.manager.interval;

import jakarta.annotation.Resource;
import online.zust.qcqcqc.utils.config.LimiterConfig;
import online.zust.qcqcqc.utils.entity.Limiter;
import online.zust.qcqcqc.utils.exception.CannotLoadLuaScriptException;
import online.zust.qcqcqc.utils.limiters.IntervalLimiterManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.connection.ReturnType;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;


/**
 * @author pqcmm
 */
@Component
@Primary
@ConditionalOnProperty(value = "limiter.type", havingValue = "redis")
public class BaseRedisIntervalLimitManager implements IntervalLimiterManager {

    private static final Logger log = LoggerFactory.getLogger(BaseRedisIntervalLimitManager.class);

    private RedisTemplate<String, Long> redisTemplate;

    private LimiterConfig limiterConfig;

    private static byte[] CHECK_INTERVAL_SCRIPT_BYTE;

    @Autowired
    public void setLimiterConfig(LimiterConfig limiterConfig) {
        this.limiterConfig = limiterConfig;
    }

    @Resource
    public void setRedisTemplate(RedisTemplate<String, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
        // 读取lua脚本
        ClassPathResource checkIntervalResource = new ClassPathResource("scripts/checkInterval.lua");
        try {
            CHECK_INTERVAL_SCRIPT_BYTE = checkIntervalResource.getInputStream().readAllBytes();
        } catch (Exception e) {
            log.error("读取lua脚本失败：{}", e.getMessage());
            throw new CannotLoadLuaScriptException("读取lua脚本失败：" + e.getMessage());
        }
    }

    /**
     * 检查两次请求的间隔时间
     *
     * @param limiter 限制器
     * @return 是否可以访问
     */
    @Override
    public boolean tryAccess(Limiter limiter) {
        boolean limitByUser = limiter.isLimitByUser();
        String key = limiter.getKey();
        long interval = limiter.getInterval();
        int maxLog = 21;
        // 生成key
        if (limitByUser) {
            key = limiterConfig.getUserKey() + "-" + key + "-interval";
        }
        return tryAlterStatus(key, interval, maxLog);
    }

    private boolean tryAlterStatus(String key, long interval, int maxLog) {
        // 若key存在，检查上一次请求和这一次请求的间隔时间
        //  如果间隔时间小于指定的间隔时间，返回false，否则返回true并添加当前时间到有序集合中
        // 若不存在，直接返回true，并添加当前时间到有序集合中
        Long result = redisTemplate.execute((RedisCallback<Long>) connection -> connection.eval(
                CHECK_INTERVAL_SCRIPT_BYTE,
                ReturnType.INTEGER,
                1,
                redisTemplate.getStringSerializer().serialize(key),
                redisTemplate.getStringSerializer().serialize(String.valueOf(interval)),
                redisTemplate.getStringSerializer().serialize(String.valueOf(System.nanoTime())),
                redisTemplate.getStringSerializer().serialize(String.valueOf(maxLog))
        ));
        return result != null && result.equals(1L);
    }
}
