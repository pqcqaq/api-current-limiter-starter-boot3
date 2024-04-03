package online.zust.qcqcqc.utils.exception;

import java.io.Serial;

/**
 * @author qcqcqc
 * tryAccess()方法抛出的异常
 */
public class ErrorTryAccessException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 8787835061592731077L;

    public ErrorTryAccessException(String message) {
        super(message);
    }
}
