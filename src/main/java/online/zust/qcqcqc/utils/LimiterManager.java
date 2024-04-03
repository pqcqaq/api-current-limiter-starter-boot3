package online.zust.qcqcqc.utils;

import online.zust.qcqcqc.utils.entity.Limiter;

/**
 * @author pqcmm
 */
public interface LimiterManager {
    /**
     * 尝试访问
     *
     * @param limiter 限制器
     * @return 是否可以访问
     */
    boolean tryAccess(Limiter limiter);
}
