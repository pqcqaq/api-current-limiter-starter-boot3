package online.zust.qcqcqc.utils.manager.current;

import jakarta.annotation.Resource;
import online.zust.qcqcqc.utils.config.LimiterConfig;
import online.zust.qcqcqc.utils.entity.Limiter;
import online.zust.qcqcqc.utils.exception.CannotLoadLuaScriptException;
import online.zust.qcqcqc.utils.limiters.CurrentLimiterManager;
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
public class BaseRedisCurrentLimitManager implements CurrentLimiterManager {

    private static final Logger log = LoggerFactory.getLogger(BaseRedisCurrentLimitManager.class);

    private RedisTemplate<String, Long> redisTemplate;

    private LimiterConfig limiterConfig;

    private static byte[] TRY_ALTER_STATUS_SCRIPT_BYTE;

    @Autowired
    public void setLimiterConfig(LimiterConfig limiterConfig) {
        this.limiterConfig = limiterConfig;
    }

    @Resource
    public void setRedisTemplate(RedisTemplate<String, Long> redisTemplate) {
        this.redisTemplate = redisTemplate;
        // 读取lua脚本
        ClassPathResource tryAlterStatusResource = new ClassPathResource("scripts/tryAlterStatus.lua");
        try {
            TRY_ALTER_STATUS_SCRIPT_BYTE = tryAlterStatusResource.getInputStream().readAllBytes();
        } catch (Exception e) {
            log.error("读取lua脚本失败：{}", e.getMessage());
            throw new CannotLoadLuaScriptException("读取lua脚本失败：" + e.getMessage());
        }
    }

    /**
     * 尝试访问
     *
     * @param limiter 限制器
     * @return 是否可以访问
     */
    @Override
    public boolean tryAccess(Limiter limiter) {
        int limitNum = limiter.getLimitNum();
        int seconds = limiter.getSeconds();
        String key = limiter.getKey();
        if (limiter.isLimitByUser()) {
            key = limiterConfig.getUserKey() + "-" + key + "-access";
        }
        return tryAlterStatus(key, limitNum, seconds);
    }

    private boolean tryAlterStatus(String key, int limitNum, int seconds) {
        // 获取当前时间，精确到纳秒
        long currentTimeNanos = System.nanoTime();

        // Lua脚本
        Long result = redisTemplate.execute((RedisCallback<Long>) connection -> connection.eval(
                TRY_ALTER_STATUS_SCRIPT_BYTE,
                ReturnType.INTEGER,
                1,
                redisTemplate.getStringSerializer().serialize(key),
                redisTemplate.getStringSerializer().serialize(String.valueOf(limitNum)),
                redisTemplate.getStringSerializer().serialize(String.valueOf(seconds)),
                redisTemplate.getStringSerializer().serialize(String.valueOf(currentTimeNanos))
        ));

        // 返回结果
        return result != null && result.equals(1L);
    }
}
