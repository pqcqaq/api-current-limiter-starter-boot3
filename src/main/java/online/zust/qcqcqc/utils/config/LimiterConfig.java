package online.zust.qcqcqc.utils.config;

/**
 * @author qcqcqc
 */
public interface LimiterConfig {
    /**
     * 在对用户进行限流时，需要重写该方法，返回用户的唯一标识
     *
     * @return 用户唯一标识
     */
    String getUserKey();
}
