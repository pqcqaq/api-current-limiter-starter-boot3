package online.zust.qcqcqc.utils.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author qcqcqc
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface IntervalLimit {
    /**
     * 两次请求的间隔时间（ms）
     * @return  long 默认100ms
     */
    long interval() default 100L;

    /**
     * 限流key
     * @return String
     */
    String key() default "";

    /**
     * 是否根据用户限流
     * @return boolean
     */
    boolean limitByUser() default false;

    /**
     * 限流提示信息
     * @return String
     */
    String msg() default "The request interval is too short. Please try again later!";
}
