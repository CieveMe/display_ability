package cn.com.yyxx.yld.supply.service;


import cn.com.yyxx.yld.supply.data.vo.StaticDataDefVO;

import java.util.List;

/**
 * 静态数据Service接口
 *
 * @author hk
 * @date 2018-12-13 15:37
 */
public interface IBmStaticDataDefService {

    /**
     * <p>
     * 根据字典code查询相关静态数据
     * </p>
     *
     * @param dddCode 字典code
     * @author zzc
     * @since 2018/12/27 9:19
     */
    List<StaticDataDefVO> listByDddCode(String dddCode);

}























