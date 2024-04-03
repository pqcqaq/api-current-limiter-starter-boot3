package online.zust.qcqcqc.utils.limiters;

import online.zust.qcqcqc.utils.LimiterManager;
import online.zust.qcqcqc.utils.entity.Limiter;

/**
 * @author qcqcqc
 */
public interface ConcurrentLimiterManager extends LimiterManager {
    /**
     * 尝试访问
     * @param limiter 限制器 并发限制器
     * @return 是否可以访问
     */
    @Override
    boolean tryAccess(Limiter limiter);
}
