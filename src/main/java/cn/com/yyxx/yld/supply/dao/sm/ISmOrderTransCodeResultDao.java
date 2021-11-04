package cn.com.yyxx.yld.supply.dao.sm;


import cn.com.yyxx.yld.supply.entity.sm.SmOrderTransCodeResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * <p>
 * 订单被扫交易结果记录表 Mapper 接口
 * </p>
 *
 * @author hk
 * @since 2019-02-12
 */
public interface ISmOrderTransCodeResultDao extends BaseMapper<SmOrderTransCodeResult> {

    /**
     * <p>
     * 根据编号list查询信息
     * </p
     *
     * @param nos 编号list
     * @return java.util.List<cn.com.yyxx.yld.scm.entity.sm.SmOrderTransCodeResult>
     * @author linmeng
     * @since 2019/2/14 17:37
     */
    List<SmOrderTransCodeResult> listByNos(@Param("nos") List<String> nos);
}
