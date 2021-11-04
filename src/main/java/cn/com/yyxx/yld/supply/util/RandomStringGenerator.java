package cn.com.yyxx.yld.supply.util;

import java.util.Random;

/**
 * <p></p>
 *
 * @author haoch
 * @version 1.0.0
 * @program yld-supply-chain-v2
 * @description
 * @date 2021-02-01 09:49
 * @since 0.1.0
 **/
public class RandomStringGenerator {

    public static final String BASE_CHAR = "abcdefghijklmnopqrstuvwxyz0123456789";
    public static final String NUM_CHAR = "0123456789";

    public RandomStringGenerator() {
    }

    public static String getRandomStringByLength(int length, String source) {
        Random random = new Random();
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < length; ++i) {
            int number = random.nextInt(source.length());
            sb.append(source.charAt(number));
        }

        return sb.toString();
    }
}
