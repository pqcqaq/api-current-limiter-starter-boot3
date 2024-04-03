package online.zust.qcqcqc.utils.manager.concurrent;

import online.zust.qcqcqc.utils.config.LimiterConfig;
import online.zust.qcqcqc.utils.entity.Limiter;
import online.zust.qcqcqc.utils.limiters.ConcurrentLimiterManager;
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
@ConditionalOnMissingBean(ConcurrentLimiterManager.class)
public class BaseMapConcurrentLimitManager implements ConcurrentLimiterManager {

    private LimiterConfig limiterConfig;

    private static final ConcurrentHashMap<String, List<Long>> STATUS_MAP = new ConcurrentHashMap<>();

    @Autowired
    public void setLimiterConfig(LimiterConfig limiterConfig) {
        this.limiterConfig = limiterConfig;
    }

    @Override
    public boolean tryAccess(Limiter limiter) {
        boolean limitByUser = limiter.isLimitByUser();
        int limitNum = limiter.getLimitNum();
        String key = limiter.getKey();
        boolean set = true;

        if (limitByUser) {
            key = limiterConfig.getUserKey() + "-" + key + "-concurrent";
        }
        return tryAlterStatus(key, limitNum, set);
    }

    private static boolean tryAlterStatus(String key, int limitNum, boolean set) {
        // 从map中获取key对应的信息
        List<Long> infos = STATUS_MAP.get(key);
        // 若key不存在，初始化key的信息，并返回true，否则检查并发数，如果超过限制返回false，否则返回true
        if (!set) {
            if (infos == null) {
                return true;
            }
            infos.remove(0);
            return true;
        }
        if (infos == null) {
            List<Long> longs = STATUS_MAP.putIfAbsent(key, new CopyOnWriteArrayList<>() {
                @Serial
                private static final long serialVersionUID = -7011652858814940405L;

                {
                    add(System.nanoTime());
                }
            });
            if (longs != null) {
                return tryAlterStatus(key, limitNum, set);
            }
            return true;
        } else {
            // key存在，判断指定时间内是否超过限制次数
            // 判断是否超过限制次数
            if (infos.size() < limitNum) {
                // 如果没有超过限制次数，更新key的信息
                long currentTime = System.nanoTime();
                infos.add(currentTime);
                return true;
            } else {
                // 如果超过限制次数，返回false
                return false;
            }
        }
    }
}
