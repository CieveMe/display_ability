package cn.com.yyxx.yld.supply.exception;

import cn.hutool.core.util.StrUtil;

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
public class NotExceptException extends RuntimeException {

    public NotExceptException(String msg, Exception e) {
        super(msg, e);
    }

    public NotExceptException(String msg) {
        super(msg);
    }

    public NotExceptException() {
        this("不期望发生的异常!");
    }

    public NotExceptException(String msg, Object... args) {
        super(StrUtil.format(msg, args));
    }
}