package online.zust.qcqcqc.utils.exception;

import java.io.Serial;

/**
 * @author pqcmm
 * 限流异常，当达到限流次数时抛出
 */
public class ApiCurrentLimitException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = -8477614628280383279L;

    public ApiCurrentLimitException(String message) {
        super(message);
    }
}
