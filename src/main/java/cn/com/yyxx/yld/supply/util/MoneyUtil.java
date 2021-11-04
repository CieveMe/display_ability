package cn.com.yyxx.yld.supply.util;

import cn.com.yyxx.yld.supply.exception.NotExceptException;

import java.math.BigDecimal;

/**
 * <p>
 *
 * </p>
 *
 * @author hz
 * @version 1.0
 * @since 2019/3/9 17:29
 */

public class MoneyUtil {

    public static BigDecimal fenToYuan(Integer fen){
        if(fen == null){
            throw new NotExceptException("参数不能为空!");
        }
        double result = fen / 100.0 ;
        return BigDecimal.valueOf(result);
    }

    public static BigDecimal fenToYuan(Long fen){
        if(fen == null){
            throw new NotExceptException("参数不能为空!");
        }
        double result = fen / 100.0 ;
        return BigDecimal.valueOf(result);
    }

    public static Integer yuanToFen(BigDecimal yuan){
        if(yuan == null){
            throw new NotExceptException("参数不能为空!");
        }
        BigDecimal multiply = yuan.multiply(BigDecimal.valueOf(100));
        return multiply.intValue();
    }
    public static Long yuanToFenLong(BigDecimal yuan){
        if(yuan == null){
            throw new NotExceptException("参数不能为空!");
        }
        BigDecimal multiply = yuan.multiply(BigDecimal.valueOf(100));
        return multiply.longValue();
    }
}
