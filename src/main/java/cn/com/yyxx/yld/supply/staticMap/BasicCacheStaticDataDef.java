package cn.com.yyxx.yld.supply.staticMap;

import cn.com.yyxx.yld.supply.dic.IStaticDataEnum;
import lombok.Getter;

/**
 * <p>
 * 基础模块缓存枚举类
 * </p>
 *
 * @author hz
 * @since 2018/12/24 17:51
 */
@Getter
public enum BasicCacheStaticDataDef implements IStaticDataEnum<Long> {

    /**
     * 静态数据缓存
     */
    CACHE_STATIC_DATA_DEF(BasicCacheConstant.CACHE_STATIC_DATA_DEF, "静态数据缓存", 86400L),
    /**
     * 行政区域缓存
     */
    CACHE_REGION_DEF(BasicCacheConstant.CACHE_REGION_DEF, "行政区域缓存", 86400L),
    /**
     * 数据字典缓存
     */
    CACHE_DATA_DICTIONARY_DEF(BasicCacheConstant.CACHE_DATA_DICTIONARY_DEF, "数据字典缓存", 86400L),

    /**
     * 授权行政区域缓存
     */
    CACHE_AUTHORIZED_REGION_INFO(BasicCacheConstant.CACHE_AUTHORIZED_REGION_INFO, "授权行政区域缓存", 86400L),

    /**
     * 认证区域银行缓存
     */
    CACHE_AUTHORIZED_REGION_BANK_REL(BasicCacheConstant.CACHE_AUTHORIZED_REGION_BANK_REL, "认证区域银行缓存", 86400L),

    /**
     *
     */
    CACHE_DICTIONARY_AND_STATIC_DATA_SYNC_QUERY_LIST(BasicCacheConstant.CACHE_DICTIONARY_AND_STATIC_DATA_SYNC_QUERY_LIST, "数据字典和静态数据缓存", 86400L),
    ;

    private BasicCacheStaticDataDef(String code, String name, Long value) {
        this.code = code;
        this.cnName = name;
        this.value = value;
    }

    private String code;
    private String cnName;
    private Long value;

    public static BasicCacheStaticDataDef queryByCode(String code) {
        return IStaticDataEnum.queryByCode(BasicCacheStaticDataDef.values(), code);
    }

}
