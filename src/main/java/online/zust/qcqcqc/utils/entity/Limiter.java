package online.zust.qcqcqc.utils.entity;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author pqcmm
 */
public class Limiter implements Serializable {
    @Serial
    private static final long serialVersionUID = 8795320155945399005L;
    /**
     * 限流次数
     */
    private int limitNum;
    /**
     * 限流时间
     */
    private int seconds;
    /**
     * 限流的key
     */
    private String key;
    /**
     * 是否根据用户限流
     */
    private boolean limitByUser;

    /**
     * 是否在访问接口之前
     */
    private boolean isBefore;
    /**
     * 间隔
     */
    private long interval;

    /**
     * 构造器
     *
     * @return Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 构造器
     */
    public static class Builder {
        private int limitNum;
        private int seconds;
        private String key;
        private boolean limitByUser;
        private boolean isBefore;
        private long interval;

        /**
         * 构造器
         */
        public Builder() {
        }

        /**
         * 构造限流次数
         *
         * @param limitNum 限流次数
         * @return Builder
         */
        public Builder limitNum(int limitNum) {
            this.limitNum = limitNum;
            return this;
        }

        /**
         * 构造限流时间
         *
         * @param seconds 限流时间
         * @return Builder
         */
        public Builder seconds(int seconds) {
            this.seconds = seconds;
            return this;
        }

        /**
         * 构造限流的key
         *
         * @param key 限流的key
         * @return Builder
         */
        public Builder key(String key) {
            this.key = key;
            return this;
        }

        /**
         * 构造是否根据用户限流
         *
         * @param limitByUser 是否根据用户限流
         * @return Builder
         */
        public Builder limitByUser(boolean limitByUser) {
            this.limitByUser = limitByUser;
            return this;
        }

        /**
         * 构造是否在访问接口之前
         *
         * @param isBefore 是否在访问接口之前
         * @return Builder
         */
        public Builder isBefore(boolean isBefore) {
            this.isBefore = isBefore;
            return this;
        }

        /**
         * 构造间隔
         *
         * @param interval 间隔
         * @return Builder
         */
        public Builder interval(long interval) {
            this.interval = interval;
            return this;
        }

        /**
         * 构造
         *
         * @return Limiter
         */
        public Limiter build() {
            return new Limiter(limitNum, seconds, key, limitByUser, isBefore, interval);
        }
    }

    /**
     * 全参构造
     *
     * @param limitNum    限流次数
     * @param seconds     限流时间
     * @param key         限流的key
     * @param limitByUser 是否根据用户限流
     */
    public Limiter(int limitNum, int seconds, String key, boolean limitByUser, boolean isBefore, long interval) {
        this.limitNum = limitNum;
        this.seconds = seconds;
        this.key = key;
        this.limitByUser = limitByUser;
        this.isBefore = isBefore;
        this.interval = interval;
    }

    /**
     * 获取
     *
     * @return limitNum
     */
    public int getLimitNum() {
        return limitNum;
    }

    /**
     * 设置
     *
     * @param limitNum 限流次数
     */
    public void setLimitNum(int limitNum) {
        this.limitNum = limitNum;
    }

    /**
     * 获取
     *
     * @return seconds
     */
    public int getSeconds() {
        return seconds;
    }

    /**
     * 设置
     *
     * @param seconds 限流时间
     */
    public void setSeconds(int seconds) {
        this.seconds = seconds;
    }

    /**
     * 获取
     *
     * @return key
     */
    public String getKey() {
        return key;
    }

    /**
     * 设置
     *
     * @param key 限流的key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 获取
     *
     * @return limitByUser
     */
    public boolean isLimitByUser() {
        return limitByUser;
    }

    /**
     * 设置
     *
     * @param limitByUser 是否根据用户限流
     */
    public void setLimitByUser(boolean limitByUser) {
        this.limitByUser = limitByUser;
    }

    /**
     * 获取
     *
     * @return isBefore
     */
    public boolean isBefore() {
        return isBefore;
    }

    /**
     * 设置
     *
     * @param isBefore 是否在访问接口之前
     */
    public void setBefore(boolean isBefore) {
        this.isBefore = isBefore;
    }

    /**
     * 获取
     *
     * @return interval
     */
    public long getInterval() {
        return interval;
    }

    /**
     * 设置
     *
     * @param interval 间隔
     */
    public void setInterval(long interval) {
        this.interval = interval;
    }

    /**
     * toString
     *
     * @return String
     */
    @Override
    public String toString() {
        return "Limiter{limitNum = " + limitNum + ", seconds = " + seconds + ", key = " + key + ", limitByUser = " + limitByUser + "}";
    }
}
