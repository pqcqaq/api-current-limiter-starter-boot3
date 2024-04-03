package online.zust.qcqcqc.utils.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author pqcmm
 * 限流注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentLimit {
    /**
     * 限流次数
     * @return int
     */
    int limitNum() default 10;

    /**
     * 限流时间
     * @return int
     */
    int seconds() default 60;

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
    String msg() default "There are currently many people , please try again later!";
}
