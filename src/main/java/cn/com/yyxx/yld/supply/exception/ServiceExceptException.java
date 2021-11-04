package cn.com.yyxx.yld.supply.exception;

import java.text.MessageFormat;

/**
 * <p></p>
 *
 * @author liujun
 * @version 1.0.0
 * @program yld-supply-v2
 * @description
 * @date 2021-01-18 16:54
 * @since 0.1.0
 **/
public class ServiceExceptException extends RuntimeException {

    private static final String ERROR_MESSAGE = "不期望发生的异常!";
    private String message;

    public ServiceExceptException() {
        this.message = "不期望发生的异常!";
    }

    public ServiceExceptException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public Throwable fillInStackTrace() {
        return this;
    }

    @Override
    public String toString() {
        return MessageFormat.format("{0}", this.message);
    }
}