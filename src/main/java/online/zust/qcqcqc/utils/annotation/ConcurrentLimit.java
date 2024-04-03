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
public @interface ConcurrentLimit {
    /**
     * 最大并发数
     * @return int
     */
    int limitNum() default 10;

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
