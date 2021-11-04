package cn.com.yyxx.yld.supply.util;


import cn.hutool.core.bean.BeanUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author hz
 * @version 1.0
 * @since 2018/12/15 11:53
 */

public class ConvertUtil {

    public static <T> T convertObject(Object sourceObj, Class<T> targetClz) {
        if (sourceObj == null) {
            return null;
        }
        if (targetClz == null) {
            throw new IllegalArgumentException("parameter clz shoud not be null");
        }
        try {
            T targetObj = targetClz.newInstance();
            BeanUtil.copyProperties(sourceObj, targetObj);
            return targetObj;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static <S, T> List<T> convertList(List<S> sources, Class<T> targetClz) {
        List<T> result = new ArrayList<>();
        if (sources == null) {
            return null;
        }
        if (sources.isEmpty()) {
            return result;
        }
        sources.forEach(temp -> {
            result.add(convertObject(temp, targetClz));
        });
        return result;
    }
}
