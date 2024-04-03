package online.zust.qcqcqc.utils.manager.interval;

import online.zust.qcqcqc.utils.config.LimiterConfig;
import online.zust.qcqcqc.utils.entity.Limiter;
import online.zust.qcqcqc.utils.limiters.IntervalLimiterManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

import java.io.Serial;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author qcqcqc
 */
@Component
@ConditionalOnMissingBean(IntervalLimiterManager.class)
public class BaseMapIntervalLimitManager implements IntervalLimiterManager {

    private LimiterConfig limiterConfig;

    private static final ConcurrentHashMap<String, List<Long>> STATUS_MAP = new ConcurrentHashMap<>();

    @Autowired
    public void setLimiterConfig(LimiterConfig limiterConfig) {
        this.limiterConfig = limiterConfig;
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
        // 生成key
        if (limitByUser) {
            key = limiterConfig.getUserKey() + "-" + key + "-interval";
        }
        return tryAlterStatus(key, interval);
    }

    private static boolean tryAlterStatus(String key, long interval) {
        // 若key存在，检查上一次请求和这一次请求的间隔时间
        List<Long> infos = STATUS_MAP.get(key);
        if (infos != null && !infos.isEmpty()) {
            long lastTime = infos.get(infos.size() - 1);
            long currentTime = System.nanoTime();
            // 如果间隔时间小于指定的间隔时间，返回false
            if (currentTime - lastTime < interval * 1000L * 1000) {
                return false;
            } else {
                // 如果间隔时间大于指定的间隔时间, 允许访问，并更新key的信息
                infos.add(currentTime);
                return true;
            }
        } else {
            // 如果key不存在，初始化key的信息
            List<Long> longs = STATUS_MAP.putIfAbsent(key, new CopyOnWriteArrayList<>() {
                @Serial
                private static final long serialVersionUID = -7011652858814940405L;

                {
                    add(System.nanoTime());
                }
            });
            if (longs != null) {
                return tryAlterStatus(key, interval);
            }
            return true;
        }
    }
}
