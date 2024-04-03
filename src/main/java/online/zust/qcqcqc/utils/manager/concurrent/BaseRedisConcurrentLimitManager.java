package online.zust.qcqcqc.utils.manager.concurrent;

import jakarta.annotation.Resource;
import online.zust.qcqcqc.utils.config.LimiterConfig;
import online.zust.qcqcqc.utils.entity.Limiter;
import online.zust.qcqcqc.utils.exception.CannotLoadLuaScriptException;
import online.zust.qcqcqc.utils.limiters.ConcurrentLimiterManager;
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
public class BaseRedisConcurrentLimitManager implements ConcurrentLimiterManager {

    private static final Logger log = LoggerFactory.getLogger(BaseRedisConcurrentLimitManager.class);

    private RedisTemplate<String, Long> redisTemplate;

    private LimiterConfig limiterConfig;

    private static byte[] CHECK_CONCURRENT_SCRIPT_BYTE;

    @Autowired
    public void setLimiterConfig(LimiterConfig limiterConfig) {
        this.limiterConfig = limiterConfig;
    }

    @Resource
    public void setRedisTemplate(RedisTemplate<String, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
        // 读取lua脚本
        ClassPathResource checkConcurrentResource = new ClassPathResource("scripts/checkConcurrent.lua");
        try {
            CHECK_CONCURRENT_SCRIPT_BYTE = checkConcurrentResource.getInputStream().readAllBytes();
        } catch (Exception e) {
            log.error("读取lua脚本失败：{}", e.getMessage());
            throw new CannotLoadLuaScriptException("读取lua脚本失败：" + e.getMessage());
        }
    }

    @Override
    public boolean tryAccess(Limiter limiter) {
        boolean limitByUser = limiter.isLimitByUser();
        int limitNum = limiter.getLimitNum();
        String key = limiter.getKey();
        boolean set = limiter.isBefore();

        if (limitByUser) {
            key = limiterConfig.getUserKey() + "-" + key + "-concurrent";
        }
        return tryAlterStatus(key, limitNum, set);
    }

    private boolean tryAlterStatus(String key, int limitNum, boolean set) {
        long currentTime = System.nanoTime();
        Long result = redisTemplate.execute((RedisCallback<Long>) connection -> connection.eval(
                CHECK_CONCURRENT_SCRIPT_BYTE,
                ReturnType.INTEGER,
                1,
                redisTemplate.getStringSerializer().serialize(key),
                redisTemplate.getStringSerializer().serialize(String.valueOf(limitNum)),
                redisTemplate.getStringSerializer().serialize(String.valueOf(set)),
                redisTemplate.getStringSerializer().serialize(String.valueOf(currentTime))
        ));
        return result != null && result.equals(1L);
    }
}
