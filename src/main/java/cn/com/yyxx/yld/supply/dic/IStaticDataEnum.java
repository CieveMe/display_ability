package cn.com.yyxx.yld.supply.dic;

import jodd.util.StringUtil;

/**
 * Created by hk on 2017/3/16.
 */
public interface IStaticDataEnum<T> {

    /**
     * 获取枚举编码
     *
     * @return
     */
    String getCode();

    /**
     * 获取枚举名称<br/>
     * 该名称不能和枚举名称冲突
     *
     * @return
     */
    String getCnName();

    /**
     * 获取枚举值
     *
     * @return
     */
    T getValue();

    /**
     * 根据编码查询枚举数据
     *
     * @param sdds 枚举数组
     * @param code 枚举编码
     * @param <S>  枚举类型
     * @return 枚举对象
     */
    static <S extends IStaticDataEnum> S queryByCode(S[] sdds, String code) {
        if (StringUtil.isNotBlank(code)) {
            for (S sdd : sdds) {
                if (StringUtil.equals(code, sdd.getCode()))
                    return sdd;
            }
        }
        return null;
    }

    /**
     * 根据数据值查询枚举数据
     *
     * @param sdds  枚举数据
     * @param value 枚举值
     * @param <S>   枚举值类型
     * @return 枚举对象
     */
    static <S extends IStaticDataEnum, T> S queryByValue(S[] sdds, T value) {
        if (value != null && sdds != null) {
            for (S sdd : sdds) {
                if (value == sdd.getValue() || sdd.getValue().equals(value) || (
                        value instanceof String &&
                                StringUtil.equals(value.toString(), sdd.getValue().toString())))
                    return sdd;
            }
        }
        return null;
    }
}
