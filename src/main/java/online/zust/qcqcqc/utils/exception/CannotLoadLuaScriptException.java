package online.zust.qcqcqc.utils.exception;

import java.io.Serial;

/**
 * @author qcqcqc
 * 无法加载lua脚本
 */
public class CannotLoadLuaScriptException extends RuntimeException{
    @Serial
    private static final long serialVersionUID = 7475489350339930399L;

    public CannotLoadLuaScriptException(String message) {
        super(message);
    }
}
