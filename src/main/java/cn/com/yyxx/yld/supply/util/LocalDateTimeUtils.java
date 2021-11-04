package cn.com.yyxx.yld.supply.util;

import cn.com.yyxx.yld.supply.exception.ServiceExceptException;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * <p>
 * LocalDateTime时间工具类
 * <p>
 *
 * @author zhang yang ze
 * @version 1.0
 * @since 2019/5/15 10:52
 */
public class LocalDateTimeUtils {

    /**
     * 日期格式yyyy-MM-dd
     */
    public static String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 日期时间格式yyyy-MM-dd HH:mm:ss
     */
    public static String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * <p>
     * Date转LocalDateTime
     * </p>
     *
     * @param date java.util.Date
     * @return java.time.LocalDateTime
     * -----------------------------------
     * @author zhang yang ze
     * @since 2019/5/15 10:55
     */
    public static LocalDateTime dateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }

    /**
     * <p>
     * LocalDateTime转换为Date
     * </p>
     *
     * @param dateTime java.time.LocalDateTime
     * @return java.util.Date
     * -----------------------------------
     * @author zhang yang ze
     * @since 2019/5/15 10:56
     */
    public static Date localDateTimeToDate(LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * <p>
     * 格式化时间-默认yyyy-MM-dd HH:mm:ss格式
     * </p>
     *
     * @param dateTime java.time.LocalDateTime
     * @return java.lang.String 时间祖父传
     * -----------------------------------
     * @author zhang yang ze
     * @since 2019/5/15 10:56
     */
    public static String formatDateTimeToStrDefault(LocalDateTime dateTime) {
        return formatDateTimeToStr(dateTime, DATE_TIME_PATTERN);
    }

    /**
     * <p>
     * 按pattern格式化时间-默认yyyy-MM-dd HH:mm:ss格式
     * </p>
     *
     * @param dateTime LocalDateTime对象
     * @param pattern  要格式化的字符串
     * @return java.lang.String 格式化后的字符串
     * -----------------------------------
     * @author zhang yang ze
     * @since 2019/5/15 11:06
     */
    public static String formatDateTimeToStr(LocalDateTime dateTime, String pattern) {
        if (dateTime == null) {
            return null;
        }
        if (pattern == null || pattern.isEmpty()) {
            pattern = DATE_TIME_PATTERN;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return dateTime.format(formatter);
    }

    public static LocalDateTime strToTime(String dateTimeStr, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            pattern = DATE_TIME_PATTERN;
        } else if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * <p>
     * 按pattern格式化时间-默认yyyy-MM-dd HH:mm:ss格式
     * </p>
     *
     * @param dateTime LocalDateTime对象
     * @param pattern  要格式化的字符串
     * @return java.time.LocalDateTime
     * -----------------------------------
     * @author zhang yang ze
     * @since 2019/5/15 11:38
     */
    public static LocalDateTime formatDatetime(LocalDateTime dateTime, String pattern) {
        if (pattern == null || pattern.isEmpty()) {
            pattern = DATE_TIME_PATTERN;
        }
        return strToTime(formatDateTimeToStrDefault(dateTime), pattern);
    }

    public static LocalDateTime formatDatetimeDefault(LocalDateTime dateTime) {
        return formatDatetime(dateTime, null);
    }

    /**
     * <p>
     * 获取今天的00:00:00
     * </p>
     *
     * @return java.lang.String
     * -----------------------------------
     * @author zhang yang ze
     * @since 2019/5/15 11:07
     */
    public static LocalDateTime getDayStart() {
        return getDayStart(LocalDateTime.now());
    }

    /**
     * <p>
     * 获取今天的23:59:59
     * </p>
     *
     * @return java.time.LocalDateTime
     * -----------------------------------
     * @author zhang yang ze
     * @since 2019/5/15 11:11
     */
    public static LocalDateTime getDayEnd() {
        return getDayEnd(LocalDateTime.now());
    }

    /**
     * <p>
     * 获取某天的00:00:00
     * </p>
     *
     * @param dateTime LocalDateTime对象
     * @return java.time.LocalDateTime
     * -----------------------------------
     * @author zhang yang ze
     * @since 2019/5/15 11:11
     */
    public static LocalDateTime getDayStart(LocalDateTime dateTime) {
        return dateTime.with(LocalTime.MIN);
    }

    /**
     * <p>
     * 获取某天的23:59:59
     * </p>
     *
     * @param dateTime LocalDateTime对象
     * @return java.time.LocalDateTime
     * -----------------------------------
     * @author zhang yang ze
     * @since 2019/5/15 11:11
     */
    public static LocalDateTime getDayEnd(LocalDateTime dateTime) {
        return dateTime.with(LocalTime.MAX);
    }

    /**
     * <p>
     * 获取本月第一天的00:00:00
     * </p>
     *
     * @return java.time.LocalDateTime
     * -----------------------------------
     * @author zhang yang ze
     * @since 2019/5/15 11:11
     */
    public static LocalDateTime getFirstDayOfMonth() {
        return LocalDateTime.now();
    }

    /**
     * <p>
     * 获取本月最后一天的23:59:59
     * </p>
     *
     * @return java.time.LocalDateTime
     * -----------------------------------
     * @author zhang yang ze
     * @since 2019/5/15 11:11
     */
    public static LocalDateTime getLastDayOfMonth() {
        return LocalDateTime.now();
    }

    /**
     * <p>
     * 获取某月第一天的00:00:00
     * </p>
     *
     * @param dateTime LocalDateTime对象
     * @return java.time.LocalDateTime
     * -----------------------------------
     * @author zhang yang ze
     * @since 2019/5/15 11:11
     */
    public static LocalDateTime getFirstDayOfMonth(LocalDateTime dateTime) {
        return dateTime.with(TemporalAdjusters.firstDayOfMonth()).with(LocalTime.MIN);
    }

    /**
     * <p>
     * 获取某月最后一天的23:59:59
     * </p>
     *
     * @param dateTime LocalDateTime对象
     * @return java.time.LocalDateTime
     * -----------------------------------
     * @author zhang yang ze
     * @since 2019/5/15 11:11
     */
    public static LocalDateTime getLastDayOfMonth(LocalDateTime dateTime) {
        return dateTime.with(TemporalAdjusters.lastDayOfMonth()).with(LocalTime.MAX);
    }

    /**
     * <p>
     * 获取昨天时间
     * </p>
     *
     * @return java.time.LocalDateTime
     * -----------------------------------
     * @author zhang yang ze
     * @since 2019/5/15 11:10
     */
    public static LocalDateTime getYesterday() {
        return getMinusDays(1L);
    }

    /**
     * <p>
     * 获取截止当前时间过去第N天的时间
     * </p>
     *
     * @param day 过去第几日
     * @return java.time.LocalDateTime
     * -----------------------------------
     * @author zhang yang ze
     * @since 2019/5/15 11:02
     */
    public static LocalDateTime getMinusDays(Long day) {
        return getMinusDays(LocalDateTime.now(), day);
    }

    /**
     * <p>
     * 获取某天过去第N天的时间
     * </p>
     *
     * @param day 过去第几日
     * @return java.time.LocalDateTime
     * -----------------------------------
     * @author zhang yang ze
     * @since 2019/5/15 11:02
     */
    public static LocalDateTime getMinusDays(LocalDateTime dateTime, Long day) {
        return formatDatetimeDefault(dateTime.minusDays(day));
    }

    /**
     * <p>
     * 获取时间间隔
     * </p>
     *
     * @param time1 时间对象
     * @param time2 时间对象
     * @return java.lang.Long
     * @throws -----------------------------------
     * @author zhang yang ze
     * @since 2019/5/15 11:28
     */
    public static Long getDayCount(LocalDateTime time1, LocalDateTime time2) {
        return Duration.between(time1, time2).toDays();
    }

    /**
     * <p>
     * 获取当前时间毫秒数
     * </p>
     *
     * @return java.lang.Long
     * -----------------------------------
     * @author zhang yang ze
     * @date 2019/9/23 22:18
     */
    public static Long defauleMile() {
        return getDataMile(LocalDateTime.now());
    }

    /**
     * <p>
     * 获取时间毫秒数
     * </p>
     *
     * @param dateTime 时间
     * @return java.lang.Long
     * -----------------------------------
     * @author zhang yang ze
     * @date 2019/9/23 22:17
     */
    public static Long getDataMile(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
    }

    /***
     * <p>
     *  获取时间毫秒差
     * </p>
     * @param dateTime 时间
     * @return java.lang.Long
     * -----------------------------------
     * @author zhang yang ze
     * @date 2019/9/24 10:47
     */
    public static Long getDefauleDifference(LocalDateTime dateTime) {
        return getTimeMillisecondDifference(dateTime, LocalDateTime.now());
    }

    /**
     * <p>
     * 时间毫秒差
     * </p>
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return java.lang.Long 返回end - begin
     * -----------------------------------
     * @author zhang yang ze
     * @date 2019/9/23 22:20
     */
    public static Long getTimeMillisecondDifference(LocalDateTime begin, LocalDateTime end) {
        if (begin == null || end == null) {
            return 0L;
        } else if (end.isBefore(begin)) {
            throw new ServiceExceptException("结束时间不能小于开始时间");
        }
        return getDataMile(end) - getDataMile(begin);
    }

    /**
     * <p>
     *
     * </p>
     *
     * @param begin        开始时间
     * @param end          结束时间
     * @param beginSection 区间开始值
     * @param endSection   区间结束值
     * @return java.lang.Boolean
     * -----------------------------------
     * @author zhang yang ze
     * @date 2019/9/23 22:26
     */
    public static Boolean isInTimeSection(LocalDateTime begin, LocalDateTime end, Long beginSection, Long endSection) {
        if (endSection < beginSection) {
            throw new ServiceExceptException("区间结束值结束不能小于或等于区间开始值");
        }
        Long m = getTimeMillisecondDifference(begin, end);
        return m >= beginSection && m <= endSection;
    }


}
