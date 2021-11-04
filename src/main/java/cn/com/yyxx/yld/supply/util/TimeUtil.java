package cn.com.yyxx.yld.supply.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * <p></p>
 *
 * @author haoch
 * @version 1.0.0
 * @program yld-supply-chain-v2
 * @description
 * @date 2021-02-01 09:48
 * @since 0.1.0
 **/
public class TimeUtil {

    public static String timeId() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        return now.format(formatter) + RandomStringGenerator
            .getRandomStringByLength(3, "0123456789");
    }

}
